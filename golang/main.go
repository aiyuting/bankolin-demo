package main

import (
	"bufio"
	"database/sql"
	"fmt"
	"log"
	"os"
	"regexp"
	"strings"

	_ "github.com/mattn/go-sqlite3"
)

type Question struct {
	Subject string
	Options []string
	Answer  string
}

var questionMap = make(map[string]Question)

func main() {
	fmt.Println("即将开始！！！")
	//处理文件
	handleFile()
	saveToDB()
	// for _, q := range questionMap {
	// 	fmt.Println(q.Subject, strings.Join(q.Options, "||"), q.Answer)
	// }

}

func saveToDB() {
	db, err := sql.Open("sqlite3", "./yee.db")
	checkErr(err)

	for _, q := range questionMap {
		stmt, err := db.Prepare(`INSERT INTO question(subject, options, answer) values(?,?,?)`)
		checkErr(err)

		res, err := stmt.Exec(q.Subject, strings.Join(q.Options, "||"), q.Answer)
		checkErr(err)
		id, err := res.LastInsertId()

		checkErr(err)

		fmt.Println(id, q.Subject)
	}

}

func handleFile() {
	file, err := os.Open("全.txt")
	if err != nil {
		log.Fatal(err)
	}
	defer file.Close()

	scanner := bufio.NewScanner(file)
	scanner.Split(bufio.ScanLines)

	q := Question{}
	for scanner.Scan() {
		text := scanner.Text()
		//判断是否是题干行
		if result, s := findSubject(text); result {
			q.Subject = s
			continue
		}

		if result, s := findOptions(text); result {
			q.Options = append(q.Options, s)
			continue
		}

		if result, s := findAnswer(text); result {
			q.Answer = s
			questionMap[q.Subject] = q
			q = Question{}
		}
	}
}
func findAnswer(line string) (result bool, s string) {
	matched, _ := regexp.MatchString(`^(标准答案\s*?：\s*?)`, line)
	if matched {
		return true, line
	}
	return false, ""
}

func findOptions(line string) (result bool, s string) {
	tempS := strings.TrimSpace(line)
	//判断选择题
	reg := regexp.MustCompile(`^\s*?(?P<index>[A-Z]\s?)\s?(?P<content>.*)`)
	matches := reg.FindStringSubmatch(tempS)
	if len(matches) > 0 {
		return true, line
	}

	reg = regexp.MustCompile(`^\s*?(正确)|^\s*?(错误)`)
	matches = reg.FindStringSubmatch(tempS)
	if len(matches) > 0 {
		return true, matches[0]
	}

	return false, ""
}

func findSubject(line string) (result bool, s string) {
	reg := regexp.MustCompile(`^\s*?(?P<index>\d+,*?\d*?\s*?\.)\s*?(?P<subject>.*)(?P<score>\s*?（.*分）)$`)
	matches := reg.FindStringSubmatch(line)
	if len(matches) > 0 {
		return true, matches[2]
	}
	return false, ""

}

func checkErr(err error) {
	if err != nil {
		panic(err)
	}
}
