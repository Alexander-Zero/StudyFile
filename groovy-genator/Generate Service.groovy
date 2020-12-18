import com.intellij.database.model.DasTable
import com.intellij.database.util.Case
import com.intellij.database.util.DasUtil

/*
 * Available context bindings:
 *   SELECTION   Iterable<DasObject>
 *   PROJECT     project
 *   FILES       files helper
 *   生成Mapper文件
 *   需设置参数
 */

packageName = "com.telefence.store"
baseMapper = "com.telefence.common.base.BaseMapper"
modelType = "Pojo"
serviceType = "Service"
typeMapping = [
        (~/(?i)int/)                      : "long",
        (~/(?i)float|double|decimal|real/): "double",
        (~/(?i)datetime|timestamp|date/)  : "Date",
        (~/(?i)time/)                     : "java.sql.Time",
        (~/(?i)/)                         : "String"
]

FILES.chooseDirectoryAndSave("Choose directory", "Choose where to store generated files") { dir ->
    SELECTION.filter { it instanceof DasTable }.each { generate(it, dir) }
}

def generate(table, dir) {
    def className = javaName(table.getName(), true)
    //设置为utf-8
    new File(dir, className + "${serviceType}.java").withPrintWriter("utf-8") { out -> generate(out, className, table.getName()) }
}

def generate(out, className, tableName) {
    out.println "package ${packageName}.service;"
    out.println ""
    out.println "import ${baseMapper}; "
    out.println "import ${packageName}.model.pojo.${className}${modelType};"
    out.println ""
    out.println ""
    out.println "public interface ${className}${serviceType} extends BaseService<${className}${modelType}> {"
    out.println ""

    out.println ""
    out.println "}"
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
