package com.lagou.edu.factory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ClasspathScanner {
    private String basePackage;
    private ClassLoader cl;

    /**
     * 初始化
     *
     * @param basePackage
     */
    public ClasspathScanner(String basePackage) {
        this.basePackage = basePackage;
        this.cl = getClass().getClassLoader();
    }

    public ClasspathScanner(String basePackage, ClassLoader cl) {
        this.basePackage = basePackage;
        this.cl = cl;
    }

    /**
     * 获取指定包下的所有字节码文件的全类名
     */
    public List<String> getFullyQualifiedClassNameList() throws IOException, ClassNotFoundException {
        return doScan(basePackage, new ArrayList<String>());
    }

    /**
     * doScan函数
     *
     * @param basePackage
     * @param nameList
     * @return
     * @throws IOException
     */
    private List<String> doScan(String basePackage, List<String> nameList) throws IOException, ClassNotFoundException {
        //先把包名转换为路径,首先得到项目的classpath
        String classpath = this.getClass().getResource("/").getPath();
        //然后把我们的包名basPach转换为路径名
        basePackage = basePackage.replace(".", File.separator);
        //然后把classpath和basePack合并
        String searchPath = classpath + basePackage;
        doPath(new File(searchPath), nameList);
        nameList = nameList.stream()
                .map(classFullpath -> classFullpath.
                        replace(classpath.replace("/", "\\")
                                .replaceFirst("\\\\", ""), "")
                        .replace("\\", ".")
                        .replace(".class", "")).collect(Collectors.toList());
        for (String s : nameList) {
            Class cls = Class.forName(s);
            System.out.println(cls);
        }
        return nameList;
    }

    private void doPath(File file, List<String> nameList) {
        if (file.isDirectory()) {//文件夹
            //文件夹我们就递归
            File[] files = file.listFiles();
            for (File f1 : files) {
                doPath(f1, nameList);
            }
        } else {//标准文件
            //标准文件我们就判断是否是class文件
            if (file.getName().endsWith(".class")) {
                //如果是class文件我们就放入我们的集合中。
                nameList.add(file.getPath());
            }
        }
    }


    /**
     * For test purpose.
     */
    public static void main(String[] args) throws Exception {
        ClasspathScanner scan = new ClasspathScanner("com.lagou.edu");
        scan.getFullyQualifiedClassNameList();
    }
}