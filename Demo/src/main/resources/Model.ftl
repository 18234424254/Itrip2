public class ${table.tableName} {
<#list table.columns as clolum>
    //${clolum.comment}
    private  ${clolum.columnType} ${clolum.columnName};

</#list>
<#list table.columns as clolum>


    public  ${clolum.columnType} get${clolum.columnName}() {
    return ${clolum.columnName};
    }

    public void set${clolum.columnName}( ${clolum.columnType} ${clolum.columnName}) {
    this.${clolum.columnName} = ${clolum.columnName};
    }
</#list>

}
