import java.util.*
import java.util.Scanner

data class Subject(var title: String, var grade: Int)

data class Student(val name: String?, val birthYear: Int, val subjects: List<Subject>) {

    val averageGrade
        get() = subjects.average { it.grade.toFloat() }
    val age
        get()= Calendar.getInstance().get(Calendar.YEAR) - birthYear

    override fun toString(): String =
        "Имя: $name, Год рождения: $birthYear, Список дисциплин: $subjects, Средняя оценка: $averageGrade"

}

data class University(val title: String, val students: MutableList<Student>) {

    val average
        get() = students.filter{ it.age in 17..20 }.average { it.averageGrade }

    val courses
        get() = students.groupBy { it.age }.mapKeys {
            when (it.key) {
                17 -> 1
                18 -> 2
                19 -> 3
                20 -> 4
                else -> throw StudentTooYoungException()
            }
        }
}

class StudentTooYoungException : Exception("Студент слишком молод")

enum class StudyProgram(private val title: String) {
    DISCIPLINE1("Мат. анализ"), DISCIPLINE2("Интеллектуальные системы"),
    DISCIPLINE3("Основы программирования"), DISCIPLINE4("Менеджмент"),
    DISCIPLINE5("Управление программными проектами");

    infix fun withGrade(grade: Int): Subject = Subject(title, grade)

}

fun <T> Iterable<T>.average(block: (T) -> Float): Float {
    var sum = 0.0
    var count = 0
    for (element in this) {
        sum += block(element)
        ++count
    }
    return (sum / count).toFloat()
}


val students = mutableListOf(
    Student("Иванов Иван", 2002, listOf(StudyProgram.DISCIPLINE1 withGrade 4, StudyProgram.DISCIPLINE2 withGrade 5)),
    Student("Петров Николай", 2002, listOf(StudyProgram.DISCIPLINE1 withGrade 4, StudyProgram.DISCIPLINE2 withGrade 4)),
    Student("Карпов Михаил", 2001, listOf(StudyProgram.DISCIPLINE4 withGrade 3, StudyProgram.DISCIPLINE5 withGrade 3)),
    Student("Пьянзов Герман", 2001, listOf(StudyProgram.DISCIPLINE4 withGrade 5, StudyProgram.DISCIPLINE5 withGrade 4)),
    Student("Уваров Роман", 2003, listOf(StudyProgram.DISCIPLINE1 withGrade 3, StudyProgram.DISCIPLINE3 withGrade 4)),
    Student("Вольнов Константин", 2004, listOf(StudyProgram.DISCIPLINE1 withGrade 5, StudyProgram.DISCIPLINE3 withGrade 4)),
    Student("Сидоров Борис", 2003, listOf(StudyProgram.DISCIPLINE1 withGrade 4, StudyProgram.DISCIPLINE3 withGrade 4))
)

object DataSource {
    val university: University by lazy {
        University("МГУ", students)
    }

    var onNewStudentListener: StudentListener? = null


    fun addStudent(students: MutableList<Student>) {

        println("Добавление студента")
        println("Введите имя : ")
        val name = readLine()
        println("Введите год рождения : ")
        val year = Scanner(System.`in`)
        val birthYear: Int = year.nextInt()
        students.add(Student(name, birthYear, listOf(StudyProgram.DISCIPLINE1 withGrade 4, StudyProgram.DISCIPLINE2 withGrade 5)))
        val addedStudent = students.last()
        onNewStudentListener?.invoke( addedStudent)
    }

}

typealias StudentListener = ((Student) -> Unit)

fun main() {

    println("универсистет: " + DataSource.university.title)
    println("\t" + DataSource.university.students.joinToString(separator = "\n\t"))
    println("Разбивка по курсам = " + DataSource.university.courses)
    println("Средняя оценка по университету = " + DataSource.university.average)

    DataSource.onNewStudentListener = {
        println("Новый студент: $it \n" + "Средняя оценка по университету: ${DataSource.university.average}")
    }
    DataSource.addStudent(students)

    println("Разбивка по курсам = " + DataSource.university.courses)
}