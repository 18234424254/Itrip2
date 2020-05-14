<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
</head>
<body>
    hello ${name}
    hello ${user.name}--${user.password}

<#--遍历集合-->
<#list  list as user>
        <#if user.name!="">
        ${user.name}--${user.password}
    </#if>
</#list>
</body>
</html>