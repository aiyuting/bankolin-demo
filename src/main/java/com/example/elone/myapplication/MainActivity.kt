package com.example.elone.myapplication

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import com.example.elone.myapplication.model.Question
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.db.classParser
import org.jetbrains.anko.db.parseList
import org.jetbrains.anko.db.select
import java.io.*

class MainActivity : AppCompatActivity() {
    private val TAG: String = "TIKU"
    private lateinit var  mListView: RecyclerView
    private lateinit var TESSBASEPATH: String
    private val CHINESELANGUAGE:String = "chi_sim"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        toolbar.title =  "YEE辅助"
        val btnShow = findViewById<Button>(R.id.btnShow)
        btnShow?.setOnClickListener { searchQuestion() }

        importDatabase()
//        initOcr()
        mListView = findViewById(R.id.question_recycler_view)
        mListView.layoutManager = LinearLayoutManager(this)
    }


    private fun searchQuestion() {
        val editText = findViewById<EditText>(R.id.searchText)
        val rows = database.use {
            select("question")
                    .whereSimple("(subject like '%"+editText.text+"%') ")
                    .exec {
                        parseList(classParser<Question>())
                    }
        }

        mListView.adapter = MainAdapter(rows)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun importDatabase() {
        // 存放数据库的目录
        var dirPath = "/data/data/com.example.elone.myapplication/databases"
        var dir = File(dirPath)
        if (!dir.exists()) {
            dir.mkdir()
        }
        // 数据库文件
        var file = File(dir, "yee.db")
        try {
            if (!file.exists()) {
                file.createNewFile()
            }
            // 加载需要导入的数据库
            var instream: InputStream = this.applicationContext.resources.openRawResource(R.raw.yee)
            var fos = FileOutputStream(file)
            var buffer  = ByteArray(instream.available())
            instream.read(buffer)
            fos.write(buffer)
            instream.close()
            fos.close()
        } catch ( e: FileNotFoundException) {
            e.printStackTrace()
        }
    }

    //代码来源：https://blog.csdn.net/aikongmeng/article/details/73323272
    private fun initOcr() {
        TESSBASEPATH = filesDir.toString() + "/tesseract/"
        checkFile(File(TESSBASEPATH + "tessdata/"), CHINESELANGUAGE)
    }

    /**
     * @param dir
     * @param language chi_sim eng
     */
    private fun checkFile(dir: File, language: String) {
        //如果目前不存在则创建方面，然后在判断训练数据文件是否存在
        if (!dir.exists() && dir.mkdirs()) {
            copyFiles(language)
        }
        if (dir.exists()) {
            val dataFilePath = TESSBASEPATH + "tessdata/" + language + ".traineddata"

            val datafile = File(dataFilePath)
            if (!datafile.exists()) {
                copyFiles(language)
            }
        }
    }

    /**
     * 把训练数据放到手机内存
     * @param language "chi_sim" ,"eng"
     */
    private fun copyFiles(language: String) {
        try {
            val filepath = TESSBASEPATH + "tessdata/" + language + ".traineddata"
            val instream = assets.open("tessdata/$language.traineddata")
            val outstream = FileOutputStream(filepath)
            val buffer = ByteArray(1024)
            var read: Int = -1
            while ( { read = instream.read(buffer); read }() != -1  ){
                outstream.write(buffer, 0, read)
            }
            outstream.flush()
            outstream.close()
            instream.close()

        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }
}

