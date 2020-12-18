import com.intellij.database.model.DasTable
import com.intellij.database.util.Case
import com.intellij.database.util.DasUtil

import java.text.SimpleDateFormat

/*
 * Available context bindings:
 *   SELECTION   Iterable<DasObject>
 *   PROJECT     project
 *   FILES       files helper
 *   生成POJO文件
 *   需定义package地址
 */

packageName = "com.telefence.store.model.pojo;"
modelType = "Pojo";
typeMapping = [
        (~/(?i)int/)                      : "long",
        (~/(?i)float|double|decimal|real/): "double",
//        (~/(?i)datetime|timestamp/)       : "java.sql.Timestamp",
//        (~/(?i)date/)                     : "java.sql.Date",
        (~/(?i)datetime|timestamp|date/)  : "Date",
        (~/(?i)time/)                     : "java.sql.Time",
        (~/(?i)/)                         : "String"
]

FILES.chooseDirectoryAndSave("Choose directory", "Choose where to store generated files") { dir ->
    SELECTION.filter { it instanceof DasTable }.each { generate(it, dir) }
}

def generate(table, dir) {
    def className = javaName(table.getName(), true)
    def fields = calcFields(table)
    //设置为utf-8
    new File(dir, className + "${modelType}.java").withPrintWriter("utf-8") { out -> generate(out, className, fields, table.getName()) }
}

def generate(out, className, fields, tableName) {

    out.println "package $packageName"
    out.println ""
    out.println "import lombok.Getter; "
    out.println "import lombok.Setter;"
    out.println ""
    out.println "import javax.persistence.Table;"
    out.println "import javax.persistence.Id;"
    out.println "import javax.persistence.Column;"
    out.println "import java.util.Date;"
    out.println ""
    out.println ""
    out.println "/**"
    out.println " * @author System Generate"
    out.println " * @version 1.0.0"
    out.println " * @date ${date()}"
    out.println " */"
    out.println "@Setter"
    out.println "@Getter"
    out.println "@Table(name = \"" + tableName + "\")"
    out.println "public class ${className}${modelType} {"
    out.println ""
    fields.each() {
        if (it.annos != "") out.println "  ${it.annos}"

        //注释
        out.println "    "
        out.println "    /**"
        out.println "     * ${it.comment}"
        out.println "     */"

        //@Id / @Column
        if (it.name == "guid") {
            out.println "    @Id"
        } else {
            out.println "    @Column"
        }
        out.println "    private ${it.type} ${it.name};"
    }
    out.println ""
//    fields.each() {
//        out.println ""
//        out.println "  public ${it.type} get${it.name.capitalize()}() {"
//        out.println "    return ${it.name};"
//        out.println "  }"
//        out.println ""
//        out.println "  public void set${it.name.capitalize()}(${it.type} ${it.name}) {"
//        out.println "    this.${it.name} = ${it.name};"
//        out.println "  }"
//        out.println ""
//    }
    out.println "}"
}

def calcFields(table) {
    DasUtil.getColumns(table).reduce([]) { fields, col ->
        def spec = Case.LOWER.apply(col.getDataType().getSpecification())
        def typeStr = typeMapping.find { p, t -> p.matcher(spec).find() }.value
        def comment = col.getComment()
        fields += [[
                           name   : javaName(col.getName(), false),
                           type   : typeStr,
                           annos  : "",
                           comment: comment]]
    }
}

def javaName(str, capitalize) {
    def s = com.intellij.psi.codeStyle.NameUtil.splitNameIntoWords(str)
            .collect { Case.LOWER.apply(it).capitalize() }
            .join("")
            .replaceAll(/[^\p{javaJavaIdentifierPart}[_]]/, "_")
    if (capitalize) {
        s = s.substring(1)
    }
    capitalize || s.length() == 1 ? s : Case.LOWER.apply(s[0]) + s[1..-1]
}

//当前日期
def date() {
    def date = new Date()
    def formatters = new SimpleDateFormat("yyyy/MM/dd")
    formatters.format(date)
}
