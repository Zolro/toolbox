package com.zyd.shiro.utils;

import com.zyd.shiro.business.annotation.FieldValue;
import com.zyd.shiro.business.annotation.TitleValue;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class PackageUtil {

    public static void main(String[] args) throws Exception {
        String packageName = "com.zyd.shiro.persistence.beans";
        String className = "PakFuck";
        genrJavaSystem(packageName,className);
        //genrInterfaceDocumentation(packageName);
    }

    public static void genrJavaSystem(String packageName){
        if(packageName==null){
            return ;
        }
        String basePackagePath = packageName.substring(0,packageName.lastIndexOf("."));

        basePackagePath = basePackagePath.substring(0,basePackagePath.lastIndexOf("."));

        String basePackagePathUrl = basePackagePath.replace(".","\\");

        String basePath = System.getProperty("user.dir")+"\\shiro-admin\\src\\main\\java\\"+basePackagePathUrl+"\\";

        List<String> classNames = getClassName(packageName, false);

        if (classNames != null) {
            for (String className : classNames) {
                String simpleClassName = className.substring(className.lastIndexOf(".")+1,className.length());
                System.out.println(simpleClassName);
                System.out.println(basePackagePath);
                System.out.println(basePath);
                //automaticGenerated(simpleClassName,basePackagePath,basePath);
            }
        }
    }

    public static void genrInterfaceDocumentation(String packageName) throws ClassNotFoundException {
        List<String> classNames = getClassName(packageName, false);
        if (classNames != null) {
            for (String className : classNames) {
                genrInterDoc(className);
            }
        }
    }

    private static void genrInterDoc(String classPath) throws ClassNotFoundException {
        Class cls = Class.forName(classPath);
        TitleValue titleValue = (TitleValue) cls.getAnnotation(TitleValue.class);
        if(null != titleValue){
            System.out.println(titleValue.name());
            Field[] fields = cls.getDeclaredFields();
            for(Field field : fields){
                FieldValue fieldValue = field.getAnnotation(FieldValue.class);
                String type = field.getGenericType().toString();
                String fieldType = type.substring(type.lastIndexOf(".")+1,type.length());
                System.out.println(field.getName()+":"+fieldValue.name()+":"+fieldType);

            }
            System.out.println();
            String classname = cls.getSimpleName();
            String nAme = classname.substring(3,classname.length());
            String name = StringUtils.toLowerCaseFirstOne(nAme);
            System.out.println("新增：/"+name+"  POST");
            System.out.println("分页查询：/"+name+"  GET");
            System.out.println("修改：/"+name+"  PATCH");
            System.out.println("删除：/"+name+"/{id}  DELETE");
            System.out.println("查询所有：/"+name+"/all  GET");
            System.out.println("excel导入：/"+name+"import  GET");

            System.out.println("传参格式："+StringUtils.lowerFirst(classname)+".id");
            System.out.println("-----------------");
        }
    }
    /**
     * PakAbnormal
     * com.zyd.shiro
     * D:\project\yjpark\shiro-admin\src\main\java\com\zyd\shiro\
     * @param className
     */
    public static void genrJavaSystem(String packageName,String className){
        if(packageName==null){
            return ;
        }
        String basePackagePath = packageName.substring(0,packageName.lastIndexOf("."));

        basePackagePath = basePackagePath.substring(0,basePackagePath.lastIndexOf("."));

        String basePackagePathUrl = basePackagePath.replace(".","\\");

        String basePath = System.getProperty("user.dir")+"\\shiro-admin\\src\\main\\java\\"+basePackagePathUrl+"\\";

        String simpleClassName = className.substring(className.lastIndexOf(".")+1,className.length());
        automaticGenerated(simpleClassName,basePackagePath,basePath);

    }

    private static void automaticGenerated(String className,String packageName,String basePath) {
                System.out.println("生成:"+className+"套装类");

                System.out.println(className+"Mapper");
                getMapperModel(packageName,className,basePath);

                System.out.println(className+"Entity");
                getEntityModel(packageName,className,basePath);

                System.out.println(className+"Service");
                getServiceModel(packageName,className,basePath);

                System.out.println(className+"ServiceImp");
                getServiceImpModel(packageName,className,basePath);

                System.out.println(className+"Controller");
                getControllerModel(packageName,className,basePath);
                System.out.println("***********************************");

    }

    private static void genrJavaFile(String content,String path){
        FileWriter writer;
        try {
            writer = new FileWriter(path);
            writer.write(content);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**************************** Mybatis *************************************************/
    private static void getMapperModel(String packagePath,String clsName,String basePath){
        String mapperPath = "persistence\\mapper\\";
        String tableName = StringUtils.humpToUnderline(clsName);
        String mapperStr = "" +
                "package [PATH].persistence.mapper;\n" +
                "\n" +
                "\n" +
                "import [PATH].persistence.beans.[CLASSNAME];\n" +
                "import [PATH].plugin.BaseMapper;\n" +
                "import [PATH].framework.object.BaseConditionVO;\n" +
                "import org.apache.ibatis.annotations.Select;\n" +
                "import org.springframework.stereotype.Repository;\n" +
                "import java.util.List;\n"+
                "\n" +
                "\n" +
                "\n" +
                "@Repository\n" +
                "public interface [CLASSNAME]Mapper extends BaseMapper<[CLASSNAME]> {\n" +
                "\n" +
                "/**\n" +
                "     * 分页查询\n" +
                "     *\n" +
                "     * @param vo\n" +
                "     * @return\n" +
                "     */\n" +
                "    @Select(\"<script>\"+\n" +
                "            \"SELECT s.* FROM [tableName] s WHERE  1 = 1 \" +\n" +
                "            \" GROUP BY s.id ORDER BY s.create_time DESC \" +\n" +
                "            \"</script>\")\n" +
                "    List<[CLASSNAME]> findPageBreakByCondition(BaseConditionVO vo);"+
                "\n" +
                "}";
        String content =  mapperStr
                .replace("[PATH]",packagePath)
                .replace("[CLASSNAME]",clsName)
                .replace("[tableName]",tableName);

        String savePath = basePath+mapperPath+clsName+"Mapper.java";

        genrJavaFile(content,savePath);

    }

    private static void getEntityModel(String packagePath,String clsName,String basePath){
        String entityPath = "business\\entity\\";
        String entityName = clsName.substring(3,clsName.length());
        String clsname = StringUtils.lowerFirst(clsName);
        String entityStr = "package [PATH].business.entity;\n" +
                "\n" +
                "import [PATH].persistence.beans.[CLASSNAME];\n" +
                "\n" +
                "import java.util.Date;\n" +
                "\n" +
                "public class [entityName] {\n" +
                "    private [CLASSNAME] [classname];\n" +
                "\n" +
                "    public [entityName]() {\n" +
                "        this.[classname] = new [CLASSNAME]();\n" +
                "    }\n" +
                "\n" +
                "    public [entityName]([CLASSNAME] [classname]) {\n" +
                "        this.[classname] = [classname];\n" +
                "    }\n" +
                "\n" +
                "    public [CLASSNAME] get[CLASSNAME]() {\n" +
                "        return this.[classname];\n" +
                "    }\n" +
                "\n" +
                "    public Long getId() {\n" +
                "        return this.[classname].getId();\n" +
                "    }\n" +
                "\n" +
                "    public void setId(Long id) {\n" +
                "        this.[classname].setId(id);\n" +
                "    }\n" +
                "    \n" +
                "    public Date getCreateTime() {\n" +
                "        return this.[classname].getCreateTime();\n" +
                "    }\n" +
                "\n" +
                "    public void setCreateTime(Date createTime) {\n" +
                "        this.[classname].setCreateTime(createTime);\n" +
                "    }\n" +
                "\n" +
                "    public Date getUpdateTime() {\n" +
                "        return this.[classname].getUpdateTime();\n" +
                "    }\n" +
                "\n" +
                "    public void setUpdateTime(Date updateTime) {\n" +
                "        this.[classname].setUpdateTime(updateTime);\n" +
                "    }\n" +
                "\n" +
                "}" ;
        String content =  entityStr
                .replace("[PATH]",packagePath)
                .replace("[CLASSNAME]",clsName)
                .replace("[classname]",clsname)
                .replace("[entityName]",entityName);

        String savePath = basePath+entityPath+entityName+".java";

        genrJavaFile(content,savePath);
    }

    private static void getServiceModel(String packagePath,String clsName,String basePath){
        String servicePath = "business\\service\\";
        String entityName = clsName.substring(3,clsName.length());
        String classname = StringUtils.lowerFirst(clsName);
        String serviceStr = "package [PATH].business.service;\n" +
                "\n" +
                "import com.github.pagehelper.PageInfo;\n" +
                "import [PATH].business.entity.[entityName];\n" +
                "import [PATH].framework.object.BaseConditionVO;\n" +
                "import [PATH].framework.object.AbstractService;\n" +
                "import com.zyd.shiro.persistence.beans.[CLASSNAME];\n"+
                "\n" +
                "public interface [CLASSNAME]Service extends AbstractService<[entityName], Long> {\n" +
                "\n" +
                "    /**\n" +
                "     * 分页查询\n" +
                "     *\n" +
                "     * @param vo\n" +
                "     * @return\n" +
                "     */\n" +
                "    PageInfo<[entityName]> findPageBreakByCondition(BaseConditionVO vo);\n" +
                "\n" +
                "\n" +
                "      void increase([CLASSNAME] [classname]);"+
                "\n"+
                "}";
        String content =  serviceStr
                .replace("[PATH]",packagePath)
                .replace("[CLASSNAME]",clsName)
                .replace("[classname]",classname)
                .replace("[entityName]",entityName);

        String savePath = basePath+servicePath+clsName+"Service.java";

        genrJavaFile(content,savePath);

    }

    private static void getServiceImpModel(String packagePath,String clsName,String basePath){
        String servicePath = "business\\service\\impl\\";
        String classname = StringUtils.lowerFirst(clsName);
        String entityName = clsName.substring(3,clsName.length());
        String entityname = StringUtils.lowerFirst(entityName);
        String serviceStrImp = "package [PATH].business.service.impl;\n" +
                "\n" +
                "import com.github.pagehelper.PageHelper;\n" +
                "import com.github.pagehelper.PageInfo;\n" +
                "import [PATH].business.entity.[entityName];\n" +
                "import [PATH].business.service.[CLASSNAME]Service;\n" +
                "import [PATH].framework.object.BaseConditionVO;\n" +
                "import [PATH].persistence.beans.[CLASSNAME];\n" +
                "import [PATH].persistence.mapper.[CLASSNAME]Mapper;\n" +
                "import lombok.extern.slf4j.Slf4j;\n" +
                "\n" +
                "import org.springframework.beans.factory.annotation.Autowired;\n" +
                "import org.springframework.stereotype.Service;\n" +
                "import org.springframework.transaction.annotation.Transactional;\n" +
                "import org.springframework.util.Assert;\n" +
                "import org.springframework.util.CollectionUtils;\n" +
                "\n" +
                "import java.util.ArrayList;\n" +
                "import java.util.Date;\n" +
                "import java.util.List;\n" +
                "\n" +
                "\n" +
                "@Slf4j\n" +
                "@Service\n" +
                "public class [CLASSNAME]ServiceImpl implements [CLASSNAME]Service {\n" +
                "\n" +
                "    @Autowired\n" +
                "    private [CLASSNAME]Mapper [classname]Mapper;\n" +
                "\n" +
                "    @Override\n" +
                "    public [entityName] insert([entityName] [entityname]) {\n" +
                "        Assert.notNull([entityname], \"对象不可为空！\");\n" +
                "        [entityname].setUpdateTime(new Date());\n" +
                "        [entityname].setCreateTime(new Date());\n" +
                "        [classname]Mapper.insertSelective([entityname].get[CLASSNAME]());\n" +
                "        return [entityname];\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public void insertList(List<[entityName]> [entityname]s) {\n" +
                "\n" +
                "    }\n" +
                "\n" +
                "\n" +
                "    /**\n" +
                "     * 根据主键字段进行删除，方法参数必须包含完整的主键属性\n" +
                "     *\n" +
                "     * @param primaryKey\n" +
                "     * @return\n" +
                "     */\n" +
                "    @Override\n" +
                "    @Transactional(rollbackFor = Exception.class)\n" +
                "    public boolean removeByPrimaryKey(Long primaryKey) {\n" +
                "        return [classname]Mapper.deleteByPrimaryKey(primaryKey) > 0;\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    @Transactional(rollbackFor = Exception.class)\n" +
                "    public boolean update([entityName] [entityname]) {\n" +
                "        Assert.notNull([entityname], \"对象不可为空！\");\n" +
                "        [entityname].setUpdateTime(new Date());\n" +
                "        return [classname]Mapper.updateByPrimaryKey([entityname].get[CLASSNAME]()) > 0;\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public boolean updateSelective([entityName] [entityname]) {\n" +
                "        Assert.notNull([entityname], \"对象不可为空！\");\n" +
                "        [entityname].setUpdateTime(new Date());\n" +
                "        return [classname]Mapper.updateByPrimaryKeySelective([entityname].get[CLASSNAME]()) > 0;\n" +
                "    }\n" +
                "\n" +
                "    /**\n" +
                "     * 根据主键字段进行查询，方法参数必须包含完整的主键属性，查询条件使用等号\n" +
                "     *\n" +
                "     * @param primaryKey\n" +
                "     * @return\n" +
                "     */\n" +
                "\n" +
                "    @Override\n" +
                "    public [entityName] getByPrimaryKey(Long primaryKey) {\n" +
                "        [CLASSNAME] [classname] = [classname]Mapper.selectByPrimaryKey(primaryKey);\n" +
                "        return null == [classname] ? null : new [entityName]([classname]);\n" +
                "    }\n" +
                "\n" +
                "\n" +
                "    /**\n" +
                "     * 根据实体中的属性进行查询，只能有一个返回值，有多个结果时抛出异常，查询条件使用等号\n" +
                "     *\n" +
                "     * @param\n" +
                "     * @return\n" +
                "     */\n" +
                "    @Override\n" +
                "    public [entityName] getOneByEntity([entityName] test) {\n" +
                "        Assert.notNull(test, \"对象不可为空！\");\n" +
                "        [CLASSNAME] [classname] = [classname]Mapper.selectOne(test.get[CLASSNAME]());\n" +
                "        return null == [classname] ? null : new [entityName]([classname]);\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public List<[entityName]> listAll() {\n" +
                "        List<[CLASSNAME]> [classname]s = [classname]Mapper.selectAll();\n" +
                "        if (CollectionUtils.isEmpty([classname]s)) {\n" +
                "            return null;\n" +
                "        }\n" +
                "        List<[entityName]> [entityname]s = new ArrayList<>();\n" +
                "        for ([CLASSNAME] [classname] : [classname]s) {\n" +
                "            [entityname]s.add(new [entityName]([classname]));\n" +
                "        }\n" +
                "        return [entityname]s;\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public List<[entityName]> listByEntity([entityName] entity) {\n" +
                "        return null;\n" +
                "    }\n" +
                "\n" +
                "\n" +
                "    /**\n" +
                "     * 分页查询\n" +
                "     *\n" +
                "     * @param vo\n" +
                "     * @return\n" +
                "     */\n" +
                "    @Override\n" +
                "    public PageInfo<[entityName]> findPageBreakByCondition(BaseConditionVO vo) {\n" +
                "        PageHelper.startPage(vo.getPageNumber(), vo.getPageSize());\n" +
                "        List<[CLASSNAME]> [classname]s = [classname]Mapper.findPageBreakByCondition(vo);\n" +
                "        if (CollectionUtils.isEmpty([classname]s)) {\n" +
                "            return null;\n" +
                "        }\n" +
                "        List<[entityName]> [entityname]s = new ArrayList<>();\n" +
                "        for ([CLASSNAME] [classname] : [classname]s) {\n" +
                "            [entityname]s.add(new [entityName]([classname]));\n" +
                "        }\n" +
                "        PageInfo bean = new PageInfo<[CLASSNAME]>([classname]s);\n" +
                "        bean.setList([entityname]s);\n" +
                "        return bean;\n" +
                "    }\n" +
                "@Override\n" +
                "    public void increase([CLASSNAME] [classname]) {\n" +
                "        [classname]Mapper.insert([classname]);\n" +
                "    }"+
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "}\n";
        String content =  serviceStrImp
                .replace("[PATH]",packagePath)
                .replace("[CLASSNAME]",clsName)
                .replace("[classname]",classname)
                .replace("[entityName]",entityName)
                .replace("[entityname]",entityname);

        String savePath = basePath+servicePath+clsName+"ServiceImpl.java";

        genrJavaFile(content,savePath);

    }

    private static void getControllerModel(String packagePath,String clsName,String basePath){
        String prefix = clsName.substring(0,3).toLowerCase();
        String entityPath = "business\\controller\\"+prefix+"\\";
        String entityName = clsName.substring(3,clsName.length());
        String clsname = StringUtils.lowerFirst(clsName);
        String entityname = StringUtils.lowerFirst(entityName);

        String controllerStr = "package [PATH].business.controller.[prefix];\n" +
                "\n" +
                "import com.github.pagehelper.PageInfo;\n" +
                "import [PATH].business.entity.[entityName];\n" +
                "import [PATH].business.service.[CLASSNAME]Service;\n" +
                "import [PATH].framework.object.BaseConditionVO;\n" +
                "import [PATH].framework.object.PageResult;\n" +
                "import [PATH].framework.object.ResponseVO;\n" +
                "import [PATH].util.ResultUtil;\n" +
                "import org.springframework.beans.factory.annotation.Autowired;\n" +
                "import org.springframework.web.bind.annotation.*;\n" +
                "import org.springframework.web.multipart.MultipartFile;\n"+
                "import com.zyd.shiro.persistence.beans.[CLASSNAME];\n"+
                "\n" +
                "import java.util.List;\n" +
                "import java.io.File;\n"+
                "import com.zyd.shiro.utils.FileUtils;\n"+
                "import com.zyd.shiro.utils.ExcelUtil;\n"+
                "import java.util.Date;\n"+
                "\n" +
                "\n" +
                "@RestController\n" +
                "@RequestMapping(\"/[entityname]\")\n" +
                "public class [CLASSNAME]Controller {\n" +
                "    @Autowired\n" +
                "    private [CLASSNAME]Service [entityname]Service;\n" +
                "\n" +
                "    @PostMapping\n" +
                "    public ResponseVO insert([entityName] [entityname]) {\n" +
                "        [entityname]Service.insert([entityname]);\n" +
                "        return ResultUtil.success(\"添加成功！\",null);\n" +
                "    }\n" +
                "\n" +
                "    @PutMapping\n" +
                "    public ResponseVO update([entityName] [entityname]) {\n" +
                "        [entityname]Service.update([entityname]);\n" +
                "        return ResultUtil.success(\"修改成功！\",null);\n" +
                "    }\n" +
                "\n" +
                "    @DeleteMapping(value = \"/{id}\")\n" +
                "    public ResponseVO delete(@PathVariable(name=\"id\") Long id){\n" +
                "        [entityname]Service.removeByPrimaryKey(id);\n" +
                "        return ResultUtil.success(\"删除成功！\",null);\n" +
                "    }\n" +
                "\n" +
                "    @GetMapping\n" +
                "    public PageResult page(BaseConditionVO vo) {\n" +
                "        PageInfo<[entityName]> pageInfo = [entityname]Service.findPageBreakByCondition(vo);\n" +
                "        return ResultUtil.tablePage(pageInfo);\n" +
                "    }\n" +
                "\n" +
                "    @GetMapping(value = \"/all\")\n" +
                "    public ResponseVO list() {\n" +
                "        List<[entityName]> [entityname]s = [entityname]Service.listAll();\n" +
                "        return ResultUtil.success(\"查询成功！\",[entityname]s);\n" +
                "    }\n" +
                "\n" +
                "    @PostMapping(value = \"/import\")\n" +
                "    public ResponseVO folderImport(MultipartFile file) throws Exception {\n" +
                "        String fileName = file.getOriginalFilename();\n" +
                "        String suffix = fileName.substring(fileName.indexOf(\".\"), fileName.length());\n" +
                "        if (suffix.equals(\".xlsx\") || suffix.equals(\".xls\")) {\n" +
                "            File newFile = new File(FileUtils.createFilePath(fileName));\n" +
                "            file.transferTo(newFile);\n" +
                "            ExcelUtil<[CLASSNAME]> excelUtil = new ExcelUtil<>();\n" +
                "            List<[CLASSNAME]> acfForms = excelUtil.importExcel(\"Sheet1\", newFile, [CLASSNAME].class);\n" +
                "            for ([CLASSNAME] a : acfForms) {\n" +
                "                a.setCreateTime(new Date());\n" +
                "                a.setUpdateTime(new Date());\n" +
                "                [entityname]Service.increase(a);\n" +
                "            }\n" +
                "            return ResultUtil.success(\"修改成功！\");\n" +
                "        }\n" +
                "        return ResultUtil.error(\"请输入excel文件!\");\n" +
                "    }"+
                "\n" +
                "}";
        String content =  controllerStr
                .replace("[PATH]",packagePath)
                .replace("[CLASSNAME]",clsName)
                .replace("[classname]",clsname)
                .replace("[entityname]",entityname)
                .replace("[entityName]",entityName)
                .replace("[prefix]",prefix);

        String savePath = basePath+entityPath+clsName+"Controller.java";
       genrJavaFile(content,savePath);
    }

    /**
     * 获取某包下（包括该包的所有子包）所有类
     *
     * @param packageName
     *            包名
     * @return 类的完整名称
     */
    public static List<String> getClassName(String packageName) {
        return getClassName(packageName, true);
    }

    /**
     * 获取某包下所有类
     *
     * @param packageName
     *            包名
     * @param childPackage
     *            是否遍历子包
     * @return 类的完整名称
     */
    public static List<String> getClassName(String packageName, boolean childPackage) {
        List<String> fileNames = null;
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        String packagePath = packageName.replace(".", "/");
        URL url = loader.getResource(packagePath);
        fileNames = getClassNameByFile(url.getPath(), null, childPackage);
        return fileNames;
    }

    /**
     * 从项目文件获取某包下所有类
     *
     * @param filePath
     *            文件路径
     * @param className
     *            类名集合
     * @param childPackage
     *            是否遍历子包
     * @return 类的完整名称
     */
    private static List<String> getClassNameByFile(String filePath, List<String> className, boolean childPackage) {
        List<String> myClassName = new ArrayList<>();
        File file = new File(filePath);
        File[] childFiles = file.listFiles();
        for (File childFile : childFiles) {
            if (childFile.isDirectory()) {
                if (childPackage) {
                    myClassName.addAll(getClassNameByFile(childFile.getPath(), myClassName, childPackage));
                }
            } else {
                String childFilePath = childFile.getPath();
                if (childFilePath.endsWith(".class")) {
                    childFilePath = childFilePath.substring(childFilePath.indexOf("\\classes") + 9,
                            childFilePath.lastIndexOf("."));
                    childFilePath = childFilePath.replace("\\", ".");
                    myClassName.add(childFilePath);
                }
            }
        }
        return myClassName;
    }




}
