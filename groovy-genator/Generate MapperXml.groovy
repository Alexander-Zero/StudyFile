import com.intellij.database.model.DasTable
import com.intellij.database.util.Case
import com.intellij.database.util.DasUtil

/*
 * Available context bindings:
 *   SELECTION   Iterable<DasObject>
 *   PROJECT     project
 *   FILES       files helper
 *   生成POJO文件
 *   需定义package地址
 */

packageName = "com.telefence.store"
mapperName = "Mapper";
modelName = "Pojo"

FILES.chooseDirectoryAndSave("Choose directory", "Choose where to store generated files") { dir ->
    SELECTION.filter { it instanceof DasTable }.each { generate(it, dir) }
}

def generate(table, dir) {
    def className = javaName(table.getName(), true)
    def fields = calcFields(table)
    //设置为utf-8
    new File(dir, className + "${mapperName}.xml").withPrintWriter("utf-8") { out -> generate(out, className, fields, table.getName()) }
}

def generate(out, className, fields, tableName) {
    out.println "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
    out.println "<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">"
    out.println "<mapper namespace=\"${packageName}.mapper.${className}${mapperName}\">"
    out.println "   <resultMap id=\"BaseResultMap\" type=\"${packageName}.model.pojo.${className}${modelName}\">"

    fields.each() {
        if (it.annos != "") out.println "  ${it.annos}"
        //<Id / Column >
        if (it.name == "guid") {
            out.println "        <id column=\"guid\" jdbcType=\"CHAR\" property=\"guid\" />"
        } else {
            out.println "        <result column=\"${it.jdbcField}\" jdbcType=\"${it.jdbcType}\" property=\"${it.name}\" />"
        }
    }

    out.println "   </resultMap>"
    out.println "</mapper>"

    out.println ""
}

def calcFields(table) {
    DasUtil.getColumns(table).reduce([]) { fields, col ->
        def spec = Case.UPPER.apply(col.getDataType().getSpecification()).replaceAll("\\(\\d+\\)", "")
        def comment = col.getComment()
        def jdbcType = jdbcType(col)
        def jdbcField = col.getName()
        fields += [[
                           name     : javaName(col.getName(), false),
                           annos    : "",
                           jdbcType : jdbcType,
                           jdbcField: jdbcField,
                           comment  : comment,]]
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

//获取jdbcType
def jdbcType(col){
    def spec = Case.UPPER.apply(col.getDataType().getSpecification()).replaceAll("\\(\\d+\\)", "")
    if(spec == "INT"){
        return "INTEGER"
    }else if(spec =="DATETIME"){
      return "TIMESTAMP"
    } else {
        return spec
    }
}
