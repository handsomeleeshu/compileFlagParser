package com.amlogic.example;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CompileFlagParser {

    /* global configs */

    /* libam_adp_adec.so config */
    public static String logFile = "/home/lishuai/work/amlogic/androidp-tv-dev/vendor/amlogic/common/external/dvb/test/am_av_test/build.log";
    public static String rootDir = "/home/lishuai/work/amlogic/androidp-tv-dev";
    public static String rootDirMacro = "ANDROID_P_SRC_ROOT";
    public static String toolChain = "bin/clang";
    public static String target = "libamadec_system.so";
    public static String cProjectFile = "/home/lishuai/workspaces/audio/libamadec_system.so/.cproject";
    public static String projectFile = "/home/lishuai/workspaces/audio/libamadec_system.so/.project";
    public static String cSourceFileSuffix = ".c";
    public static String cppSourceFileSuffix = ".cpp";
    public static String objSuffix = ".o";
    public static String staticLibSuffix = ".a";
    public static String sharedLibSuffix = ".so";
    public static String excutableSuffix = "";

//  /* libam_adp_adec.so config */
//    public static String logFile = "/home/lishuai/work/amlogic/androidp-tv-dev/hardware/amlogic/LibAudio/build.log";
//    public static String rootDir = "/home/lishuai/work/amlogic/androidp-tv-dev";
//    public static String rootDirMacro = "ANDROID_P_SRC_ROOT";
//    public static String toolChain = "bin/clang";
//    public static String target = "libfaad_sys.so";
//    public static String cProjectFile = "/home/lishuai/workspaces/audio/libfaad_sys.so/.cproject";
//    public static String projectFile = "/home/lishuai/workspaces/audio/libfaad_sys.so/.project";
//    public static String cSourceFileSuffix = ".c";
//    public static String cppSourceFileSuffix = ".cpp";
//    public static String objSuffix = ".o";
//    public static String staticLibSuffix = ".a";
//    public static String sharedLibSuffix = ".so";
//    public static String excutableSuffix = "";

//  /* am_av_test config */
//    public static String logFile = "/home/lishuai/work/amlogic/androidp-tv-dev/vendor/amlogic/common/external/dvb/test/am_av_test/build.log";
//    public static String rootDir = "/home/lishuai/work/amlogic/androidp-tv-dev";
//    public static String rootDirMacro = "ANDROID_P_SRC_ROOT";
//    public static String toolChain = "bin/clang";
//    public static String target = "am_av_test";
//    public static String cProjectFile = "/home/lishuai/workspaces/audio/am_av_test/.cproject";
//    public static String projectFile = "/home/lishuai/workspaces/audio/am_av_test/.project";
//    public static String cSourceFileSuffix = ".c";
//    public static String cppSourceFileSuffix = ".cpp";
//    public static String objSuffix = ".o";
//    public static String staticLibSuffix = ".a";
//    public static String sharedLibSuffix = ".so";
//    public static String excutableSuffix = "";

//    /* test opus decode config */
//    public static String logFile = "/Users/lishuai/work/temp/build_opus.log";
//    public static String rootDir = "/Users/lishuai/work/code/opus-1.3.1";
//    public static String rootDirMacro = "OPUS_SRC_ROOT";
//    public static String toolChain = "arm-linux-androideabi-g";
//    public static String target = "test_opus_decode";
//    public static String cProjectFile = "/Users/lishuai/work/eclipse_ws/audio/test_opus_decode/.cproject";
//    public static String projectFile = "/Users/lishuai/work/eclipse_ws/audio/test_opus_decode/.project";
//    public static String cSourceFileSuffix = ".c";
//    public static String cppSourceFileSuffix = ".cpp";
//    public static String objSuffix = ".o";
//    public static String staticLibSuffix = ".la";
//    public static String sharedLibSuffix = ".so";
//    public static String excutableSuffix = "";

//    /* lib opus config */
//    public static String logFile = "/Users/lishuai/work/temp/build_opus.log";
//    public static String rootDir = "/Users/lishuai/work/code/opus-1.3.1";
//    public static String rootDirMacro = "OPUS_SRC_ROOT";
//    public static String toolChain = "arm-linux-androideabi-g";
//    public static String target = "libopus.la";
//    public static String cProjectFile = "/Users/lishuai/work/eclipse_ws/audio/libopus/.cproject";
//    public static String projectFile = "/Users/lishuai/work/eclipse_ws/audio/libopus/.project";
//    public static String cSourceFileSuffix = ".c";
//    public static String cppSourceFileSuffix = ".cpp";
//    public static String objSuffix = ".lo";
//    public static String staticLibSuffix = ".la";
//    public static String sharedLibSuffix = ".so";
//    public static String excutableSuffix = "";

//    /* fdkaac config */
//    public static String logFile = "/Users/lishuai/work/temp/build_aac.log";
//    public static String rootDir = "/Users/lishuai/work/code/fdk-aac-2.0.0";
//    public static String rootDirMacro = "FDKAAC_SRC_ROOT";
//    public static String toolChain = "arm-linux-androideabi-g";
//    public static String target = "libfdk-aac.la";
//    public static String cProjectFile = "/Users/lishuai/work/eclipse_ws/audio/libaac/.cproject";
//    public static String projectFile = "/Users/lishuai/work/eclipse_ws/audio/libaac/.project";
//    public static String cSourceFileSuffix = ".c";
//    public static String cppSourceFileSuffix = ".cpp";
//    public static String objSuffix = ".lo";
//    public static String staticLibSuffix = ".la";
//    public static String sharedLibSuffix = ".so";
//    public static String excutableSuffix = "";

    public static void main(String[] args) {
        String str = null, pwdDir = null;
        String[] linkedFlags = null;
        BufferedReader reader = null;

        String cCompileTool = "", cppCompileTool = "", linkerTool = "";
        LinkedList<String> buildLog = new LinkedList<String>();
        LinkedList<String> objList = new LinkedList<String>();
        String rootDirectoryMacro = "${" + rootDirMacro + "}";

        MutuallyLinkedList<String> otherLinkedFlags = new MutuallyLinkedList<String>();
        MutuallyLinkedList<String> defineCFlags = new MutuallyLinkedList<String>();
        MutuallyLinkedList<String> undefineCFlags = new MutuallyLinkedList<String>();
        MutuallyLinkedList<String> includeCFlags = new MutuallyLinkedList<String>();
        MutuallyLinkedList<String> includeHFileCFlags = new MutuallyLinkedList<String>();
        MutuallyLinkedList<String> otherCFlags = new MutuallyLinkedList<String>();
        MutuallyLinkedList<String> defineCppFlags = new MutuallyLinkedList<String>();
        MutuallyLinkedList<String> undefineCppFlags = new MutuallyLinkedList<String>();
        MutuallyLinkedList<String> includeCppFlags = new MutuallyLinkedList<String>();
        MutuallyLinkedList<String> includeHFileCppFlags = new MutuallyLinkedList<String>();
        MutuallyLinkedList<String> otherCppFlags = new MutuallyLinkedList<String>();

        try {
            reader = new BufferedReader(new FileReader(logFile));
            while ((str = reader.readLine()) != null) {
                Pattern p = Pattern.compile("\t");
                Matcher m = p.matcher(str);
                str = m.replaceAll(" ");
                buildLog.addLast(str);
            }

            LinkedList<String> stringList = grep(buildLog, target);
            stringList = grep(stringList, toolChain);
            stringList = grep(stringList, " -o ");

            if (stringList.isEmpty()) {
                reader.close();
                System.out.println("not found " + target + " linking flags !!!");
                return;
            } else {
                boolean linkedFlagFound = false;
                for (int i = 0; i < stringList.size(); i++) {
                    linkedFlags = splitCmd(stringList.get(i));

                    for (int j = 0; j < linkedFlags.length; j++) {
                        if (linkedFlags[j].equals("-o") && linkedFlags[j + 1].replace("\"", "").replace("'", "").endsWith(target)) {
                            linkedFlagFound = true;
                            break;
                        }
                    }

                    if (linkedFlagFound) {
                        break;
                    }
                }

                if (!linkedFlagFound) {
                    reader.close();
                    System.out.println("not found " + target + " linking flags !!!");
                    return;
                }
            }

            if (!grep(linkedFlags, "pwd=").isEmpty()) {
                pwdDir = grep(linkedFlags, "pwd=").get(0).replaceFirst("pwd=", "");
            } else {
                pwdDir = rootDir;
            }

            int i;
            int size = linkedFlags.length;
            for (i = 0; i < size; i++) {
                str = linkedFlags[i];
                if (str.contains(toolChain)) {
                    linkerTool = str;
                    break;
                }
            }

            for (i++; i < size; i++) {
                str = linkedFlags[i];

                if (str.equals("")) {
                    continue;
                }

                if (str.replace("\"", "").replace("'", "").endsWith(objSuffix)) {
                    str = str.replace("\"", "").replace("'", "");
                    if (str.startsWith("./")) {
                        str = str.substring(2, str.length());
                    }
                    LinkedList<String> list;
                    stringList = grep(buildLog, str);
                    stringList = grep(stringList, " -o ");
                    stringList = grep(stringList, " -c ");

                    String[] sourceFileName = str.replace("\"", "").replace("'", "").split("/");
                    /* find match cpp source file */
                    String grepString = sourceFileName[sourceFileName.length - 1].substring(0, sourceFileName[sourceFileName.length - 1].length() - objSuffix.length()) + cppSourceFileSuffix;
                    list = grep(stringList, grepString);
                    if (!list.isEmpty()) {
                        String[] sourceFileFullName = list.getFirst().split(" ");
                        objList.addLast(removeHeadTailSpaces(grep(sourceFileFullName, grepString).getLast().replace("\"", "").replace("'", "")));
                        objList.addLast(removeHeadTailSpaces(str.replace("\"", "").replace("'", "")));
                        continue;
                    }
                    /* find match c source file */
                    grepString = sourceFileName[sourceFileName.length - 1].substring(0, sourceFileName[sourceFileName.length - 1].length() - objSuffix.length()) + cSourceFileSuffix;
                    list = grep(stringList, grepString);
                    if (!list.isEmpty()) {
                        String[] sourceFileFullName = list.getFirst().split(" ");
                        objList.addLast(removeHeadTailSpaces(grep(sourceFileFullName, grepString).getLast().replace("\"", "").replace("'", "")));
                        objList.addLast(removeHeadTailSpaces(str.replace("\"", "").replace("'", "")));
                        continue;
                    }

                    /* no match source file, add the obj file to other flags */
                    otherLinkedFlags.addLast(ajustDir(str.replace("\"", "").replace("'", ""), pwdDir, rootDir, rootDirectoryMacro));
                    continue;
                }

                if (str.equals("-L")) {
                    otherLinkedFlags.addLast(str + " " + ajustDir(linkedFlags[i + 1].replace("\"", "").replace("'", ""), pwdDir, rootDir, rootDirectoryMacro));
                    i++;
                    continue;
                } else if (str.startsWith("-L")) {
                    otherLinkedFlags.addLast("-L " + ajustDir(str.substring(2, str.length() - 1).replace("\"", "").replace("'", ""), pwdDir, rootDir, rootDirectoryMacro));
                    continue;
                }

                if (str.equals("-l")) {
                    otherLinkedFlags.addLast(str + linkedFlags[i + 1]);
                    i++;
                    continue;
                } else if (str.startsWith("-l")) {
                    otherLinkedFlags.addLast(str);
                    continue;
                }

                if (str.equals("-target")) {
                    otherLinkedFlags.addLast(str + " " + linkedFlags[i + 1]);
                    i++;
                    continue;
                } else if (str.startsWith("-target")) {
                    otherLinkedFlags.addLast(str);
                    continue;
                }

                if (str.equals("-rpath")) {
                    otherLinkedFlags.addLast(str + " " + linkedFlags[i + 1]);
                    i++;
                    continue;
                } else if (str.startsWith("-rpath")) {
                    otherLinkedFlags.addLast(str);
                    continue;
                }

                if (str.equals("-Wl,-soname")) {
                    otherLinkedFlags.addLast(str + " " + linkedFlags[i + 1]);
                    i++;
                    continue;
                } else if (str.startsWith("-Wl,-soname")) {
                    otherLinkedFlags.addLast(str);
                    continue;
                }

                if (str.equals("-o")) {
                    i++;
                    continue;
                }

                if (str.startsWith("-version-info")) {
                    i++;
                    continue;
                }

                if (str.equals("-B")) {
                    otherLinkedFlags.addLast(str + ajustDir(linkedFlags[i + 1], pwdDir, rootDir, rootDirectoryMacro));
                    i++;
                    continue;
                } else if (str.startsWith("-B")) {
                    otherLinkedFlags.addLast("-B" + ajustDir(str.substring(2, str.length()), pwdDir, rootDir, rootDirectoryMacro));
                    continue;
                }

                if (str.equals("-Wl,-rpath-link=")) {
                    otherLinkedFlags.addLast(str + ajustDir(linkedFlags[i + 1], pwdDir, rootDir, rootDirectoryMacro));
                    i++;
                    continue;
                } else if (str.startsWith("-Wl,-rpath-link=")) {
                    otherLinkedFlags.addLast("-Wl,-rpath-link=" + ajustDir(str.substring(16, str.length()), pwdDir, rootDir, rootDirectoryMacro));
                    continue;
                }

                if (!str.startsWith("-")) {
                    otherLinkedFlags.addLast(ajustDir(linkedFlags[i], pwdDir, rootDir, rootDirectoryMacro));
                    continue;
                }

                if (str.startsWith("&") || str.startsWith(">")) {
                    /* linked cmd finished */
                    break;
                }

                otherLinkedFlags.addLast(str);
            }

            for (i = 0; i < objList.size(); i += 2) {
                int k;
                String[] cxxCompileFlags;
                MutuallyLinkedList<String> cxxDefineFlags;
                MutuallyLinkedList<String> cxxUndefFlags;
                MutuallyLinkedList<String> cxxOtherFlags;
                MutuallyLinkedList<String> cxxIncludeFlags;
                MutuallyLinkedList<String> cxxIncludeHFileFlags;

                stringList = grep(buildLog, objList.get(i));
                stringList = grep(stringList, objList.get(i + 1));
                stringList = grep(stringList, toolChain);
                stringList = grep(stringList, " -o ");

                if (objList.get(i).replace("\"", "").replace("'", "").endsWith(cSourceFileSuffix)) {
                    cxxDefineFlags = defineCFlags;
                    cxxUndefFlags = undefineCFlags;
                    cxxOtherFlags = otherCFlags;
                    cxxIncludeFlags = includeCFlags;
                    cxxIncludeHFileFlags = includeHFileCFlags;
                } else if (objList.get(i).replace("\"", "").replace("'", "").endsWith(cppSourceFileSuffix)) {
                    cxxDefineFlags = defineCppFlags;
                    cxxUndefFlags = undefineCppFlags;
                    cxxOtherFlags = otherCppFlags;
                    cxxIncludeFlags = includeCppFlags;
                    cxxIncludeHFileFlags = includeHFileCppFlags;
                } else {
                    System.out.println("not found " + objList.get(i) + "in build log !!!");
                    continue;
                }

                cxxCompileFlags = splitCmd(stringList.get(0));

                if (!grep(cxxCompileFlags, "pwd=").isEmpty()) {
                    pwdDir = grep(cxxCompileFlags, "pwd=").get(0).replaceFirst("pwd=", "");
                } else {
                    pwdDir = rootDir;
                }

                size = cxxCompileFlags.length;
                for (k = 0; k < size; k++) {
                    str = cxxCompileFlags[k];
                    if (str.contains(toolChain)) {
                        if (cxxDefineFlags == defineCFlags) {
                            cCompileTool = str;
                        } else {
                            cppCompileTool = str;
                        }
                        break;
                    }
                }
                for (k++; k < size; k++) {
                    str = cxxCompileFlags[k];

                    if (str.equals("-I")) {
                        cxxIncludeFlags.addLast(ajustDir(cxxCompileFlags[k + 1].replace("\"", "").replace("'", ""), pwdDir, rootDir, rootDirectoryMacro));
                        k++;
                        continue;
                    } else if (str.startsWith("-I")) {
                        cxxIncludeFlags.addLast(ajustDir(str.substring(2, str.length()).replace("\"", "").replace("'", ""), pwdDir, rootDir, rootDirectoryMacro));
                        continue;
                    }

                    if (str.equals("-include")) {
                        cxxIncludeHFileFlags.addLast(ajustDir(cxxCompileFlags[k + 1].replace("\"", "").replace("'", ""), pwdDir, rootDir, rootDirectoryMacro));
                        k++;
                        continue;
                    } else if (str.startsWith("-include")) {
                        cxxIncludeHFileFlags.addLast(ajustDir(str.substring(2, str.length()).replace("\"", "").replace("'", ""), pwdDir, rootDir, rootDirectoryMacro));
                        continue;
                    }

                    if (str.startsWith("-D")) {
                        int m = k;
                        if (str.contains("=\\\"")) {
                            while (m < size) {
                                if (cxxCompileFlags[m].endsWith("\\\"")) {
                                    break;
                                }
                                m++;
                                str += cxxCompileFlags[m];
                            }
                            /* not support -D string case now */
                            str = str.substring(0, str.indexOf("="));
                        }
                        k = m;
                        cxxDefineFlags.addLast(str.substring(2, str.length()));
                        continue;
                    }

                    if (str.startsWith("-U")) {
                        int m = k;
                        if (str.contains("=\\\"")) {
                            while (m < size) {
                                if (cxxCompileFlags[m].endsWith("\\\"")) {
                                    break;
                                }
                                m++;
                                str += cxxCompileFlags[m];
                            }
                            /* not support -U string case now */
                            str = str.substring(0, str.indexOf("="));
                        }
                        k = m;
                        cxxUndefFlags.addLast(str.substring(2, str.length()));
                        continue;
                    }

                    if (str.equals("-isystem")) {
                        cxxOtherFlags.addLast(str + " " + ajustDir(cxxCompileFlags[k + 1].replace("\"", "").replace("'", ""), pwdDir, rootDir, rootDirectoryMacro));
                        k++;
                        continue;
                    } else if (str.startsWith("-isystem")) {
                        cxxOtherFlags.addLast("-isystem " + " " + ajustDir(str.substring(2, str.length()).replace("\"", "").replace("'", ""), pwdDir, rootDir, rootDirectoryMacro));
                        continue;
                    }

                    if (str.equals("-B")) {
                        cxxOtherFlags.addLast(str + ajustDir(cxxCompileFlags[k + 1].replace("\"", "").replace("'", ""), pwdDir, rootDir, rootDirectoryMacro));
                        k++;
                        continue;
                    } else if (str.startsWith("-B")) {
                        cxxOtherFlags.addLast("-B" + ajustDir(str.substring(2, str.length()).replace("\"", "").replace("'", ""), pwdDir, rootDir, rootDirectoryMacro));
                        continue;
                    }

                    if (str.equals("-target")) {
                        cxxOtherFlags.addLast(str);
                        cxxOtherFlags.addLast(cxxCompileFlags[k + 1]);
                        k++;
                        continue;
                    }

                    if (str.equals("-MF") || str.equals("-MT")) {
                        k++;
                        continue;
                    } else if (str.startsWith("-MF") || str.startsWith("-MT")) {
                        continue;
                    }

                    if (str.startsWith("-M") && str.length() <= 4) {
                        continue;
                    }

                    if (str.startsWith("-g") && str.length() <= 5) {
                        continue;
                    }

                    if (str.startsWith("-O") && str.length() <= 3) {
                        continue;
                    }

                    if (str.startsWith("-fdebug")) {
                        k++;
                        continue;
                    }

                    if (str.replace("\"", "").replace("'", "").endsWith(".d")) {
                        continue;
                    }

                    if (str.equals("-o")) {
                        k++;
                        continue;
                    }

                    if (str.replace("\"", "").replace("'", "").equals(objList.get(i))) {
                        continue;
                    }

                    if (str.startsWith("&") || str.startsWith(">")) {
                        /* compile cmd finished */
                        break;
                    }

                    cxxOtherFlags.addLast(str);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("objList");
        System.out.println(objList);
        System.out.println("");

        System.out.println("otherLinkedFlags");
        System.out.println(otherLinkedFlags);
        System.out.println("");

        System.out.println("defineCFlags");
        System.out.println(defineCFlags);
        System.out.println("");

        System.out.println("undefineCFlags");
        System.out.println(undefineCFlags);
        System.out.println("");

        System.out.println("includeCFlags");
        System.out.println(includeCFlags);
        System.out.println("");

        System.out.println("includeHFileCFlags");
        System.out.println(includeHFileCFlags);
        System.out.println("");

        System.out.println("otherCFlags");
        System.out.println(otherCFlags);
        System.out.println("");

        System.out.println("defineCppFlags");
        System.out.println(defineCppFlags);
        System.out.println("");

        System.out.println("undefineCppFlags");
        System.out.println(undefineCppFlags);
        System.out.println("");

        System.out.println("includeCppFlags");
        System.out.println(includeCppFlags);
        System.out.println("");

        System.out.println("includeHFileCppFlags");
        System.out.println(includeHFileCppFlags);
        System.out.println("");

        System.out.println("otherCppFlags");
        System.out.println(otherCppFlags);
        System.out.println("");

        CProjectFileGenerator cProjctFile = new CProjectFileGenerator(CompileFlagParser.cProjectFile);
        cProjctFile.setTarget(target);
        cProjctFile.setCIncludeFlags(includeCFlags);
        cProjctFile.setCDefineFlags(defineCFlags);
        cProjctFile.setCOtherFlags(otherCFlags);
        cProjctFile.setCHFileIncludeFlags(includeHFileCFlags);
        cProjctFile.setCUndefFlags(undefineCFlags);
        cProjctFile.setCppDefineFlags(defineCppFlags);
        cProjctFile.setCppOtherFlags(otherCppFlags);
        cProjctFile.setCppIncludeFlags(includeCppFlags);
        cProjctFile.setCppHFileIncludeFlags(includeHFileCppFlags);
        cProjctFile.setCppUndefFlags(undefineCppFlags);
        cProjctFile.setLinkedFlags(otherLinkedFlags);
        cProjctFile.setCCompileTool(cCompileTool);
        cProjctFile.setCppCompileTool(cppCompileTool);
        cProjctFile.setLinkerTool(linkerTool);
        cProjctFile.generate();

        ProjectFileGenerator projectFile = new ProjectFileGenerator(CompileFlagParser.projectFile);
        projectFile.setRootDir(rootDir);
        projectFile.setRootDirMacro(rootDirMacro);
        projectFile.setTarget(target);
        projectFile.setSrcFiles(objList);
        projectFile.generate();
    }

    static String[] concat(String[] a, String[] b) {
        String[] c = new String[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }

    private static int typeOfQuote(String str) {
        int i = 0;
        String quote = "\\";
        String tail = quote;

        while (str.endsWith(tail)) {
            i++;
            tail += quote;
        }
        return i & 1;
    }

    private static String[] splitCmd(String cmd) {
        int i = 0;
        String[] subString = cmd.split("\"");

        for (i = 1; i < subString.length;) {
            if (((i & 0x1) == 1) && (typeOfQuote(subString[i - 1]) == typeOfQuote(subString[i]))) {
                subString[i] = "\"" + subString[i] + "\"";
                i += 2;
                continue;
            }
            subString[i] += "\"" + subString[i] + "\"";
            subString[i + 1] = "";
            subString = removeEmptyLines(subString);
        }

        String[] ret = { "" };
        for (i = 0; i < subString.length; i += 2) {
            ret = concat(ret, subString[i].split(" "));
            if (i + 1 < subString.length) {
                if (subString[i].endsWith(" ")) {
                    String[] temp = { subString[i + 1] };
                    ret = concat(ret, temp);
                } else {
                    ret[ret.length - 1] += subString[i + 1];
                }
            }
        }

        return removeEmptyLines(ret);
    }

    private static String ajustDir(String srcDir, String pwdDir, String rootDir, String rootDirMacro) throws Exception {
        if (srcDir.startsWith("\"")) {
            if (!srcDir.endsWith("\"")) {
                throw new Exception("src directory has invalid format!");
            }
            srcDir = srcDir.substring(1, srcDir.length() - 1);
        }
        if (srcDir.startsWith("./")) {
            srcDir = srcDir.substring(2, srcDir.length());
        }
        if (!srcDir.startsWith("/")) {
            srcDir = pwdDir + "/" + srcDir;
        }

        return rootDirMacro + srcDir.replaceFirst(rootDir, "");
    }

    private static String removeHeadTailSpaces(String str) {
        while (str.startsWith(" ")) {
            str = str.substring(1, str.length());
        }
        while (str.endsWith(" ")) {
            str = str.substring(0, str.length() - 1);
        }

        return str;
    }

    private static LinkedList<String> grep(LinkedList<String> stringList, String grepStr) {
        String str;
        LinkedList<String> list = new LinkedList<String>();
        int size = stringList.size();

        for (int i = 0; i < size; i++) {
            str = stringList.get(i);
            if (str.contains(grepStr)) {
                list.add(str);
            }
        }

        return list;
    }

    private static LinkedList<String> grep(String[] stringList, String grepStr) {
        String str;
        LinkedList<String> list = new LinkedList<String>();
        int size = stringList.length;

        for (int i = 0; i < size; i++) {
            str = stringList[i];
            if (str.contains(grepStr)) {
                list.add(str);
            }
        }

        return list;
    }

    private static String[] removeEmptyLines(String[] stringList) {
        int i = 0, j = 0;

        String[] newList = new String[stringList.length];
        for (i = 0; i < stringList.length; i++) {
            if (!stringList[i].equals("")) {
                newList[j++] = stringList[i];
            }
        }

        String[] retList = new String[j];

        for (i = 0; i < j; i++) {
            retList[i] = newList[i];
        }

        return retList;
    }

    private static class MutuallyLinkedList<E> extends LinkedList<E> {
        /**
         *
         */
        private static final long serialVersionUID = 9125569061053691698L;

        public void addLast(E e) {
            if (!contains(e)) {
                super.addLast(e);
            }
        }
    }

    private static class ProjectFileGenerator {
        FileWriter writer;
        String targetName;
        LinkedList<String> srcFiles;
        String rootDir, rootDirMacro;

        public ProjectFileGenerator(String fileName) {
            try {
                File file = new File(fileName);
                file.createNewFile();
                writer = new FileWriter(file);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        public void setTarget(String target) {
            this.targetName = target;
        }

        public void setRootDir(String rootDir) {
            this.rootDir = rootDir;
        }

        public void setRootDirMacro(String macro) {
            this.rootDirMacro = macro;
        }

        public void setSrcFiles(LinkedList<String> srcFiles) {
            this.srcFiles = new LinkedList<String>();
            for (int i = 0; i < srcFiles.size(); i += 2) {
                if (srcFiles.get(i).startsWith("/")) {
                    String str = srcFiles.get(i).replaceFirst(rootDir, "");
                    while (str.startsWith("/")) {
                        str = str.substring(1, str.length());
                    }
                    this.srcFiles.addLast(str);
                } else {
                    this.srcFiles.addLast(srcFiles.get(i));
                }
            }
        }

        public void generate() {
            generateHead(targetName);
            generateSourcesLink(srcFiles, rootDirMacro);
            generateTail(rootDir, rootDirMacro);
        }

        private void generateHead(String targetName) {
            try {
                writer.write(
                        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                                "<projectDescription>\n" +
                                "	<name>" + targetName + "</name>\n" +
                                "	<comment></comment>\n" +
                                "	<projects>\n" +
                                "	</projects>\n" +
                                "	<buildSpec>\n" +
                                "		<buildCommand>\n" +
                                "			<name>org.eclipse.cdt.managedbuilder.core.genmakebuilder</name>\n" +
                                "			<triggers>clean,full,incremental,</triggers>\n" +
                                "			<arguments>\n" +
                                "			</arguments>\n" +
                                "		</buildCommand>\n" +
                                "		<buildCommand>\n" +
                                "			<name>org.eclipse.cdt.managedbuilder.core.ScannerConfigBuilder</name>\n" +
                                "			<triggers>full,incremental,</triggers>\n" +
                                "			<arguments>\n" +
                                "			</arguments>\n" +
                                "		</buildCommand>\n" +
                                "	</buildSpec>\n" +
                                "	<natures>\n" +
                                "		<nature>org.eclipse.cdt.core.cnature</nature>\n" +
                                "		<nature>org.eclipse.cdt.core.ccnature</nature>\n" +
                                "		<nature>org.eclipse.cdt.managedbuilder.core.managedBuildNature</nature>\n" +
                                "		<nature>org.eclipse.cdt.managedbuilder.core.ScannerConfigNature</nature>\n" +
                                "	</natures>\n" +
                                "");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        private void generateSourcesLink(LinkedList<String> srcFiles, String rootDirMacro) {
            MutuallyLinkedList<String> folders = new MutuallyLinkedList<String>();
            for (int i = 0; i < srcFiles.size(); i++) {
                String[] subFolder = srcFiles.get(i).split("/");
                subFolder = removeEmptyLines(subFolder);
                String folder = subFolder[0];
                for (int j = 1; j < subFolder.length; j++) {
                    folders.addLast(folder);
                    folder = folder + "/" + subFolder[j];
                }
            }
            try {
                writer.write(
                        "	<linkedResources>\n" +
                                "");

                for (int i = 0; i < folders.size(); i++) {
                    writer.write(
                            "		<link>\n" +
                                    "			<name>" + folders.get(i) + "</name>\n" +
                                    "			<type>2</type>\n" +
                                    "			<locationURI>virtual:/virtual</locationURI>\n" +
                                    "		</link>\n" +
                                    "");
                }
                for (int i = 0; i < srcFiles.size(); i++) {
                    writer.write(
                            "		<link>\n" +
                                    "			<name>" + srcFiles.get(i) + "</name>\n" +
                                    "			<type>1</type>\n" +
                                    "			<locationURI>" + rootDirMacro + "/" + srcFiles.get(i) + "</locationURI>\n" +
                                    "		</link>\n" +
                                    "");
                }
                writer.write(
                        "	</linkedResources>\n" +
                                "");

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        private void generateTail(String rootDir, String rootDirMacro) {
            try {
                writer.write(
                        "	<variableList>\n" +
                                "		<variable>\n" +
                                "			<name>" + rootDirMacro + "</name>\n" +
                                "			<value>file:" + rootDir + "</value>\n" +
                                "		</variable>\n" +
                                "	</variableList>\n" +
                                "</projectDescription>\n" +
                                "");
                writer.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    private static class CProjectFileGenerator {
        FileWriter writer;
        String target;
        String linkCmd = "gcc", cCompileCmd = "gcc", cppCompileCmd = "g++", asCmd = "", cmdPrefix = "", cmdPath = "";
        LinkedList<String> linkedFlags;
        LinkedList<String> cDefineFlags;
        LinkedList<String> cUndefineFlags;
        LinkedList<String> cIncludeFlags;
        LinkedList<String> cHFileIncludeFlags;
        LinkedList<String> cOtherFlags;
        LinkedList<String> cppDefineFlags;
        LinkedList<String> cppUndefineFlags;
        LinkedList<String> cppIncludeFlags;
        LinkedList<String> cppHFileIncludeFlags;
        LinkedList<String> cppOtherFlags;

        public CProjectFileGenerator(String fileName) {
            try {
                File file = new File(fileName);
                file.createNewFile();
                writer = new FileWriter(file);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        public void setTarget(String target) {
            this.target = target;
        }

        public void setCCompileTool(String toolPath) {
            if (toolPath.isEmpty()) {
                return;
            }
            String[] folders = toolPath.split("/");
            cCompileCmd = folders[folders.length - 1];
            cmdPath = toolPath.substring(0, toolPath.length() - cCompileCmd.length());
            String[] cmdParts = cCompileCmd.split("-");
            cmdPrefix = cCompileCmd.substring(0, cCompileCmd.length() - cmdParts[cmdParts.length - 1].length());
            cCompileCmd = cmdParts[cmdParts.length - 1];
        }

        public void setCppCompileTool(String toolPath) {
            if (toolPath.isEmpty()) {
                return;
            }
            String[] folders = toolPath.split("/");
            cppCompileCmd = folders[folders.length - 1];
            cmdPath = toolPath.substring(0, toolPath.length() - cppCompileCmd.length());
            String[] cmdParts = cppCompileCmd.split("-");
            cmdPrefix = cppCompileCmd.substring(0, cppCompileCmd.length() - cmdParts[cmdParts.length - 1].length());
            cppCompileCmd = cmdParts[cmdParts.length - 1];
        }

        public void setLinkerTool(String toolPath) {
            if (toolPath.isEmpty()) {
                return;
            }
            String[] folders = toolPath.split("/");
            linkCmd = folders[folders.length - 1];
            cmdPath = toolPath.substring(0, toolPath.length() - linkCmd.length());
            String[] cmdParts = linkCmd.split("-");
            cmdPrefix = linkCmd.substring(0, linkCmd.length() - cmdParts[cmdParts.length - 1].length());
            linkCmd = cmdParts[cmdParts.length - 1];
        }

        public void setCDefineFlags(LinkedList<String> flags) {
            cDefineFlags = flags;
        }

        public void setCUndefFlags(LinkedList<String> flags) {
            cUndefineFlags = flags;
        }

        public void setCIncludeFlags(LinkedList<String> flags) {
            cIncludeFlags = flags;
        }

        public void setCHFileIncludeFlags(LinkedList<String> flags) {
            cHFileIncludeFlags = flags;
        }

        public void setCOtherFlags(LinkedList<String> flags) {
            cOtherFlags = flags;
        }

        public void setCppDefineFlags(LinkedList<String> flags) {
            cppDefineFlags = flags;
        }

        public void setCppUndefFlags(LinkedList<String> flags) {
            cppUndefineFlags = flags;
        }

        public void setCppIncludeFlags(LinkedList<String> flags) {
            cppIncludeFlags = flags;
        }

        public void setCppHFileIncludeFlags(LinkedList<String> flags) {
            cppHFileIncludeFlags = flags;
        }

        public void setCppOtherFlags(LinkedList<String> flags) {
            cppOtherFlags = flags;
        }

        public void setLinkedFlags(LinkedList<String> flags) {
            linkedFlags = flags;
        }

        public void generate() {
            generateHead(target);
            generateCIncludeFlags(cIncludeFlags);
            generateCDefineFlags(cDefineFlags);
            generateCOtherFlags(cOtherFlags);
            generateCIncludeFileFlags(cHFileIncludeFlags);
            generateCUndefineFlags(cUndefineFlags);
            generateCppDefineFlags(cppDefineFlags);
            generateCppOtherFlags(cppOtherFlags);
            generateCppIncludeFlags(cppIncludeFlags);
            generateCppIncludeFileFlags(cppHFileIncludeFlags);
            generateCppUndefineFlags(cppUndefineFlags);
            generateLinkerFlags(linkedFlags);
            generateTail();
        }

        private void generateHead(String targetName) {
            String buildType = "";
            String buildExt = "";
            if (targetName.endsWith(staticLibSuffix)) {
                buildType = "staticLib";
            } else if (targetName.endsWith(sharedLibSuffix)) {
                buildType = "sharedLib";
            } else {
            }
            try {
                writer.write(
                        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
                                "<?fileVersion 4.0.0?><cproject storage_type_id=\"org.eclipse.cdt.core.XmlProjectDescriptionStorage\">\n" +
                                "	<storageModule moduleId=\"org.eclipse.cdt.core.settings\">\n" +
                                "		<cconfiguration id=\"cdt.managedbuild.config.gnu.cross.exe.debug.1648944120\">\n" +
                                "			<storageModule buildSystemId=\"org.eclipse.cdt.managedbuilder.core.configurationDataProvider\" id=\"cdt.managedbuild.config.gnu.cross.exe.debug.1648944120\" moduleId=\"org.eclipse.cdt.core.settings\" name=\"Debug\">\n" +
                                "				<externalSettings>\n" +
                                "					<externalSetting>\n");
                writer.write(
                        "						<entry flags=\"VALUE_WORKSPACE_PATH\" kind=\"includePath\" name=\"/" + targetName + "\"/>\n" +
                                "						<entry flags=\"VALUE_WORKSPACE_PATH\" kind=\"libraryPath\" name=\"/" + targetName + "/Debug\"/>\n" +
                                "						<entry flags=\"RESOLVED\" kind=\"libraryFile\" name=\"" + targetName + "\" srcPrefixMapping=\"\" srcRootPath=\"\"/>\n");
                writer.write(
                        "					</externalSetting>\n" +
                                "				</externalSettings>\n" +
                                "				<extensions>\n" +
                                "					<extension id=\"org.eclipse.cdt.core.ELF\" point=\"org.eclipse.cdt.core.BinaryParser\"/>\n" +
                                "					<extension id=\"org.eclipse.cdt.core.GmakeErrorParser\" point=\"org.eclipse.cdt.core.ErrorParser\"/>\n" +
                                "					<extension id=\"org.eclipse.cdt.core.CWDLocator\" point=\"org.eclipse.cdt.core.ErrorParser\"/>\n" +
                                "					<extension id=\"org.eclipse.cdt.core.GCCErrorParser\" point=\"org.eclipse.cdt.core.ErrorParser\"/>\n" +
                                "					<extension id=\"org.eclipse.cdt.core.GASErrorParser\" point=\"org.eclipse.cdt.core.ErrorParser\"/>\n" +
                                "					<extension id=\"org.eclipse.cdt.core.GLDErrorParser\" point=\"org.eclipse.cdt.core.ErrorParser\"/>\n" +
                                "				</extensions>\n" +
                                "			</storageModule>\n" +
                                "			<storageModule moduleId=\"cdtBuildSystem\" version=\"4.0.0\">\n" +
                                "				<configuration artifactExtension=\"" + buildExt + "\" artifactName=\"${ProjName}\" buildArtefactType=\"org.eclipse.cdt.build.core.buildArtefactType." + buildType + "\" buildProperties=\"org.eclipse.cdt.build.core.buildArtefactType=org.eclipse.cdt.build.core.buildArtefactType." + buildType + ",org.eclipse.cdt.build.core.buildType=org.eclipse.cdt.build.core.buildType.debug\" cleanCommand=\"rm -rf\" description=\"\" errorParsers=\"org.eclipse.cdt.core.GmakeErrorParser;org.eclipse.cdt.core.CWDLocator;org.eclipse.cdt.core.GCCErrorParser;org.eclipse.cdt.core.GASErrorParser;org.eclipse.cdt.core.GLDErrorParser\" id=\"cdt.managedbuild.config.gnu.cross.exe.debug.1648944120\" name=\"Debug\" parent=\"cdt.managedbuild.config.gnu.cross.exe.debug\" postannouncebuildStep=\"\" postbuildStep=\"\" preannouncebuildStep=\"\" prebuildStep=\"\">\n" +
                                "					<folderInfo id=\"cdt.managedbuild.config.gnu.cross.exe.debug.1648944120.\" name=\"/\" resourcePath=\"\">\n" +
                                "						<toolChain errorParsers=\"\" id=\"cdt.managedbuild.toolchain.gnu.cross.exe.debug.1874158812\" name=\"Cross GCC\" superClass=\"cdt.managedbuild.toolchain.gnu.cross.exe.debug\">\n" +
                                "							<option id=\"cdt.managedbuild.option.gnu.cross.prefix.2134984782\" name=\"Prefix\" superClass=\"cdt.managedbuild.option.gnu.cross.prefix\" useByScannerDiscovery=\"false\" value=\"" + cmdPrefix + "\" valueType=\"string\"/>\n" +
                                "							<option id=\"cdt.managedbuild.option.gnu.cross.path.1704021835\" name=\"Path\" superClass=\"cdt.managedbuild.option.gnu.cross.path\" useByScannerDiscovery=\"false\" value=\"" + cmdPath + "\" valueType=\"string\"/>\n" +
                                "							<targetPlatform archList=\"all\" binaryParser=\"org.eclipse.cdt.core.ELF\" id=\"cdt.managedbuild.targetPlatform.gnu.cross.910914318\" isAbstract=\"false\" osList=\"all\" superClass=\"cdt.managedbuild.targetPlatform.gnu.cross\"/>\n" +
                                "							<builder buildPath=\"${workspace_loc:/test}/Debug\" errorParsers=\"org.eclipse.cdt.core.GmakeErrorParser;org.eclipse.cdt.core.CWDLocator\" id=\"cdt.managedbuild.builder.gnu.cross.1519326106\" keepEnvironmentInBuildfile=\"false\" managedBuildOn=\"true\" name=\"Gnu Make Builder\" superClass=\"cdt.managedbuild.builder.gnu.cross\">\n" +
                                "								<outputEntries>\n" +
                                "									<entry flags=\"VALUE_WORKSPACE_PATH|RESOLVED\" kind=\"outputPath\" name=\"Debug\"/>\n" +
                                "								</outputEntries>\n" +
                                "							</builder>\n" +
                                "							<tool command=\"" + cCompileCmd + "\" commandLinePattern=\"${COMMAND} ${FLAGS} ${OUTPUT_FLAG} ${OUTPUT_PREFIX}${OUTPUT} ${INPUTS}\" errorParsers=\"org.eclipse.cdt.core.GCCErrorParser\" id=\"cdt.managedbuild.tool.gnu.cross.c.compiler.1740708202\" name=\"Cross GCC Compiler\" superClass=\"cdt.managedbuild.tool.gnu.cross.c.compiler\">\n" +
                                "								<option defaultValue=\"gnu.c.optimization.level.none\" id=\"gnu.c.compiler.option.optimization.level.851583359\" name=\"Optimization Level\" superClass=\"gnu.c.compiler.option.optimization.level\" useByScannerDiscovery=\"false\" value=\"gnu.c.optimization.level.none\" valueType=\"enumerated\"/>\n" +
                                "								<option id=\"gnu.c.compiler.option.debugging.level.2092104319\" name=\"Debug Level\" superClass=\"gnu.c.compiler.option.debugging.level\" useByScannerDiscovery=\"false\" value=\"gnu.c.debugging.level.max\" valueType=\"enumerated\"/>\n" +
                                "								<option id=\"gnu.c.compiler.option.dialect.std.1875712474\" name=\"Language standard\" superClass=\"gnu.c.compiler.option.dialect.std\" useByScannerDiscovery=\"true\" value=\"gnu.c.compiler.dialect.default\" valueType=\"enumerated\"/>\n");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        private void generateCIncludeFlags(LinkedList<String> flags) {
            try {
                writer.write(
                        "								<option id=\"gnu.c.compiler.option.include.paths.1677072826\" name=\"Include paths (-I)\" superClass=\"gnu.c.compiler.option.include.paths\" useByScannerDiscovery=\"false\" valueType=\"includePath\">\n");
                for (int i = 0; i < flags.size(); i++) {
                    writer.write(
                            "									<listOptionValue builtIn=\"false\" value=\"" + flags.get(i) + "\"/>\n");
                }
                writer.write(
                        "								</option>\n");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        private void generateCDefineFlags(LinkedList<String> flags) {
            try {
                writer.write(
                        "								<option id=\"gnu.c.compiler.option.preprocessor.def.symbols.159527443\" name=\"Defined symbols (-D)\" superClass=\"gnu.c.compiler.option.preprocessor.def.symbols\" useByScannerDiscovery=\"false\" valueType=\"definedSymbols\">\n");
                for (int i = 0; i < flags.size(); i++) {
                    writer.write(
                            "									<listOptionValue builtIn=\"false\" value=\"" + flags.get(i) + "\"/>\n");
                }
                writer.write(
                        "								</option>\n" +
                                "								<option id=\"gnu.c.compiler.option.misc.pic.1443281071\" name=\"Position Independent Code (-fPIC)\" superClass=\"gnu.c.compiler.option.misc.pic\" useByScannerDiscovery=\"false\" value=\"false\" valueType=\"boolean\"/>\n");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        private void generateCOtherFlags(LinkedList<String> flags) {
            try {
                writer.write(
                        "								<option id=\"gnu.c.compiler.option.misc.other.1565630258\" name=\"Other flags\" superClass=\"gnu.c.compiler.option.misc.other\" useByScannerDiscovery=\"false\" value=\"");
                for (int i = 0; i < flags.size(); i++) {
                    writer.write(flags.get(i) + " ");
                }
                writer.write(
                        "\" valueType=\"string\"/>\n");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        private void generateCIncludeFileFlags(LinkedList<String> flags) {
            try {
                writer.write(
                        "								<option id=\"gnu.c.compiler.option.include.files.282279272\" name=\"Include files (-include)\" superClass=\"gnu.c.compiler.option.include.files\" useByScannerDiscovery=\"false\" valueType=\"includeFiles\">\n");
                for (int i = 0; i < flags.size(); i++) {
                    writer.write(
                            "									<listOptionValue builtIn=\"false\" value=\"" + flags.get(i) + "\"/>\n");
                }
                writer.write(
                        "								</option>\n" +
                                "								<option id=\"gnu.c.compiler.option.preprocessor.nostdinc.1452453262\" name=\"Do not search system directories (-nostdinc)\" superClass=\"gnu.c.compiler.option.preprocessor.nostdinc\" useByScannerDiscovery=\"false\" value=\"false\" valueType=\"boolean\"/>\n" +
                                "								<option id=\"gnu.c.compiler.option.debugging.gprof.486374969\" name=\"Generate gprof information (-pg)\" superClass=\"gnu.c.compiler.option.debugging.gprof\" useByScannerDiscovery=\"false\" value=\"false\" valueType=\"boolean\"/>\n");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        private void generateCUndefineFlags(LinkedList<String> flags) {
            try {
                writer.write(
                        "								<option id=\"gnu.c.compiler.option.preprocessor.undef.symbol.281459856\" name=\"Undefined symbols (-U)\" superClass=\"gnu.c.compiler.option.preprocessor.undef.symbol\" useByScannerDiscovery=\"false\" valueType=\"undefDefinedSymbols\">\n");
                for (int i = 0; i < flags.size(); i++) {
                    writer.write(
                            "									<listOptionValue builtIn=\"false\" value=\"" + flags.get(i) + "\"/>\n");
                }
                writer.write(
                        "								</option>\n" +
                                "								<option id=\"gnu.c.compiler.option.dialect.flags.429697936\" name=\"Other dialect flags\" superClass=\"gnu.c.compiler.option.dialect.flags\" useByScannerDiscovery=\"true\" value=\"\" valueType=\"string\"/>\n" +
                                "								<option id=\"gnu.c.compiler.option.warnings.allwarn.966035331\" superClass=\"gnu.c.compiler.option.warnings.allwarn\" value=\"false\" valueType=\"boolean\"/>\n" +
                                "								<inputType id=\"cdt.managedbuild.tool.gnu.c.compiler.input.2027022934\" superClass=\"cdt.managedbuild.tool.gnu.c.compiler.input\"/>\n" +
                                "							</tool>\n");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        private void generateCppDefineFlags(LinkedList<String> flags) {
            try {
                writer.write(
                        "							<tool command=\"" + cppCompileCmd + "\" commandLinePattern=\"${COMMAND} ${FLAGS} ${OUTPUT_FLAG} ${OUTPUT_PREFIX}${OUTPUT} ${INPUTS}\" errorParsers=\"org.eclipse.cdt.core.GCCErrorParser\" id=\"cdt.managedbuild.tool.gnu.cross.cpp.compiler.1531969055\" name=\"Cross G++ Compiler\" superClass=\"cdt.managedbuild.tool.gnu.cross.cpp.compiler\">\n" +
                                "								<option id=\"gnu.cpp.compiler.option.optimization.level.404914194\" name=\"Optimization Level\" superClass=\"gnu.cpp.compiler.option.optimization.level\" useByScannerDiscovery=\"false\" value=\"gnu.cpp.compiler.optimization.level.none\" valueType=\"enumerated\"/>\n" +
                                "								<option id=\"gnu.cpp.compiler.option.debugging.level.957528395\" name=\"Debug Level\" superClass=\"gnu.cpp.compiler.option.debugging.level\" useByScannerDiscovery=\"false\" value=\"gnu.cpp.compiler.debugging.level.max\" valueType=\"enumerated\"/>\n" +
                                "								<option id=\"gnu.cpp.compiler.option.preprocessor.def.1057535825\" name=\"Defined symbols (-D)\" superClass=\"gnu.cpp.compiler.option.preprocessor.def\" useByScannerDiscovery=\"false\" valueType=\"definedSymbols\">\n");
                for (int i = 0; i < flags.size(); i++) {
                    writer.write(
                            "									<listOptionValue builtIn=\"false\" value=\"" + flags.get(i) + "\"/>\n");
                }
                writer.write(
                        "								</option>\n" +
                                "								<option id=\"gnu.cpp.compiler.option.dialect.std.611462728\" name=\"Language standard\" superClass=\"gnu.cpp.compiler.option.dialect.std\" useByScannerDiscovery=\"true\" value=\"gnu.cpp.compiler.dialect.default\" valueType=\"enumerated\"/>\n");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        private void generateCppOtherFlags(LinkedList<String> flags) {
            try {
                writer.write(
                        "								<option id=\"gnu.cpp.compiler.option.other.other.1767830441\" name=\"Other flags\" superClass=\"gnu.cpp.compiler.option.other.other\" useByScannerDiscovery=\"false\" value=\"");
                for (int i = 0; i < flags.size(); i++) {
                    writer.write(flags.get(i) + " ");
                }
                writer.write(
                        "\" valueType=\"string\"/>\n" +
                                "								<option id=\"gnu.cpp.compiler.option.other.pic.65604555\" name=\"Position Independent Code (-fPIC)\" superClass=\"gnu.cpp.compiler.option.other.pic\" useByScannerDiscovery=\"false\" value=\"false\" valueType=\"boolean\"/>\n");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        private void generateCppIncludeFlags(LinkedList<String> flags) {
            try {
                writer.write(
                        "								<option id=\"gnu.cpp.compiler.option.include.paths.73418692\" name=\"Include paths (-I)\" superClass=\"gnu.cpp.compiler.option.include.paths\" useByScannerDiscovery=\"false\" valueType=\"includePath\">\n");
                for (int i = 0; i < flags.size(); i++) {
                    writer.write(
                            "									<listOptionValue builtIn=\"false\" value=\"" + flags.get(i) + "\"/>\n");
                }
                writer.write(
                        "								</option>\n");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        private void generateCppIncludeFileFlags(LinkedList<String> flags) {
            try {
                writer.write(
                        "								<option id=\"gnu.cpp.compiler.option.include.files.1018725910\" name=\"Include files (-include)\" superClass=\"gnu.cpp.compiler.option.include.files\" useByScannerDiscovery=\"false\" valueType=\"includeFiles\">\n");
                for (int i = 0; i < flags.size(); i++) {
                    writer.write(
                            "									<listOptionValue builtIn=\"false\" value=\"" + flags.get(i) + "\"/>\n");
                }
                writer.write(
                        "								</option>\n" +
                                "								<option id=\"gnu.cpp.compiler.option.preprocessor.nostdinc.846830269\" name=\"Do not search system directories (-nostdinc)\" superClass=\"gnu.cpp.compiler.option.preprocessor.nostdinc\" useByScannerDiscovery=\"false\" value=\"false\" valueType=\"boolean\"/>\n" +
                                "								<option id=\"gnu.cpp.compiler.option.debugging.gprof.1289434470\" name=\"Generate gprof information (-pg)\" superClass=\"gnu.cpp.compiler.option.debugging.gprof\" useByScannerDiscovery=\"false\" value=\"false\" valueType=\"boolean\"/>\n");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        private void generateCppUndefineFlags(LinkedList<String> flags) {
            try {
                writer.write(
                        "								<option id=\"gnu.cpp.compiler.option.preprocessor.undef.958428965\" name=\"Undefined symbols (-U)\" superClass=\"gnu.cpp.compiler.option.preprocessor.undef\" useByScannerDiscovery=\"false\" valueType=\"undefDefinedSymbols\">\n");
                for (int i = 0; i < flags.size(); i++) {
                    writer.write(
                            "									<listOptionValue builtIn=\"false\" value=\"" + flags.get(i) + "\"/>\n");
                }
                writer.write(
                        "								</option>\n" +
                                "								<option id=\"gnu.cpp.compiler.option.warnings.allwarn.1882802321\" superClass=\"gnu.cpp.compiler.option.warnings.allwarn\" value=\"false\" valueType=\"boolean\"/>\n" +
                                "								<inputType id=\"cdt.managedbuild.tool.gnu.cpp.compiler.input.1035246757\" superClass=\"cdt.managedbuild.tool.gnu.cpp.compiler.input\"/>\n" +
                                "							</tool>\n" +
                                "							<tool id=\"cdt.managedbuild.tool.gnu.cross.c.linker.220925711\" name=\"Cross GCC Linker\" superClass=\"cdt.managedbuild.tool.gnu.cross.c.linker\">\n" +
                                "								<option defaultValue=\"false\" id=\"gnu.c.link.option.shared.956095380\" superClass=\"gnu.c.link.option.shared\" valueType=\"boolean\"/>\n" +
                                "							</tool>\n" +
                                "							<tool command=\"" + linkCmd + "\" commandLinePattern=\"${COMMAND} ${FLAGS} ${OUTPUT_FLAG} ${OUTPUT_PREFIX}${OUTPUT} ${INPUTS}\" errorParsers=\"org.eclipse.cdt.core.GLDErrorParser\" id=\"cdt.managedbuild.tool.gnu.cross.cpp.linker.1932220023\" name=\"Cross G++ Linker\" superClass=\"cdt.managedbuild.tool.gnu.cross.cpp.linker\">\n" +
                                "								<option id=\"gnu.cpp.link.option.nostdlibs.711078146\" name=\"No startup or default libs (-nostdlib)\" superClass=\"gnu.cpp.link.option.nostdlibs\" useByScannerDiscovery=\"false\" value=\"false\" valueType=\"boolean\"/>\n" +
                                "								<option id=\"gnu.cpp.link.option.libs.1376236940\" name=\"Libraries (-l)\" superClass=\"gnu.cpp.link.option.libs\" useByScannerDiscovery=\"false\"/>\n" +
                                "								<option id=\"gnu.cpp.link.option.paths.1027592646\" name=\"Library search path (-L)\" superClass=\"gnu.cpp.link.option.paths\" useByScannerDiscovery=\"false\"/>\n" +
                                "								<option id=\"gnu.cpp.link.option.other.1068980229\" name=\"Other options (-Xlinker [option])\" superClass=\"gnu.cpp.link.option.other\" useByScannerDiscovery=\"false\"/>\n" +
                                "								<option id=\"gnu.cpp.link.option.userobjs.527590099\" name=\"Other objects\" superClass=\"gnu.cpp.link.option.userobjs\" useByScannerDiscovery=\"false\"/>\n");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        private void generateLinkerFlags(LinkedList<String> flags) {
            try {
                writer.write(
                        "								<option id=\"gnu.cpp.link.option.flags.1587373543\" name=\"Linker flags\" superClass=\"gnu.cpp.link.option.flags\" useByScannerDiscovery=\"false\" value=\"");
                for (int i = 0; i < flags.size(); i++) {
                    writer.write(flags.get(i) + " ");
                }
                writer.write(
                        "\" valueType=\"string\"/>\n");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        private void generateTail() {
            try {
                writer.write(
                        "								<option id=\"gnu.cpp.link.option.debugging.gprof.341724871\" name=\"Generate gprof information (-pg)\" superClass=\"gnu.cpp.link.option.debugging.gprof\" useByScannerDiscovery=\"false\" value=\"false\" valueType=\"boolean\"/>\n" +
                                "								<option defaultValue=\"false\" id=\"gnu.cpp.link.option.shared.1065527157\" superClass=\"gnu.cpp.link.option.shared\" valueType=\"boolean\"/>\n" +
                                "								<inputType id=\"cdt.managedbuild.tool.gnu.cpp.linker.input.338375993\" superClass=\"cdt.managedbuild.tool.gnu.cpp.linker.input\">\n" +
                                "									<additionalInput kind=\"additionalinputdependency\" paths=\"$(USER_OBJS)\"/>\n" +
                                "									<additionalInput kind=\"additionalinput\" paths=\"$(LIBS)\"/>\n" +
                                "								</inputType>\n" +
                                "								<outputType id=\"cdt.managedbuild.tool.gnu.cpp.linker.output.343839160\" outputPrefix=\"\" superClass=\"cdt.managedbuild.tool.gnu.cpp.linker.output\"/>\n" +
                                "							</tool>\n" +
                                "							<tool id=\"cdt.managedbuild.tool.gnu.cross.archiver.654163789\" name=\"Cross GCC Archiver\" superClass=\"cdt.managedbuild.tool.gnu.cross.archiver\">\n" +
                                "								<outputType id=\"cdt.managedbuild.tool.gnu.archiver.output.1890400257\" outputPrefix=\"\" superClass=\"cdt.managedbuild.tool.gnu.archiver.output\"/>\n" +
                                "							</tool>\n" +
                                "							<tool command=\"" + asCmd + "\" commandLinePattern=\"${COMMAND} ${FLAGS} ${OUTPUT_FLAG} ${OUTPUT_PREFIX}${OUTPUT} ${INPUTS}\" errorParsers=\"org.eclipse.cdt.core.GASErrorParser\" id=\"cdt.managedbuild.tool.gnu.cross.assembler.1127778392\" name=\"Cross GCC Assembler\" superClass=\"cdt.managedbuild.tool.gnu.cross.assembler\">\n" +
                                "								<option id=\"gnu.both.asm.option.include.paths.2101698361\" name=\"Include paths (-I)\" superClass=\"gnu.both.asm.option.include.paths\" useByScannerDiscovery=\"false\" valueType=\"includePath\"/>\n" +
                                "								<option id=\"gnu.both.asm.option.flags.1652156105\" name=\"Assembler flags\" superClass=\"gnu.both.asm.option.flags\" useByScannerDiscovery=\"false\" value=\"\" valueType=\"string\"/>\n" +
                                "								<inputType id=\"cdt.managedbuild.tool.gnu.assembler.input.844382421\" superClass=\"cdt.managedbuild.tool.gnu.assembler.input\"/>\n" +
                                "							</tool>\n" +
                                "						</toolChain>\n" +
                                "					</folderInfo>\n" +
                                "					<sourceEntries>\n" +
                                "					</sourceEntries>\n" +
                                "				</configuration>\n" +
                                "			</storageModule>\n" +
                                "			<storageModule moduleId=\"org.eclipse.cdt.core.externalSettings\"/>\n" +
                                "		</cconfiguration>\n" +
                                "	</storageModule>\n" +
                                "	<storageModule moduleId=\"cdtBuildSystem\" version=\"4.0.0\">\n" +
                                "		<project id=\"test.cdt.managedbuild.target.gnu.cross.exe.517284510\" name=\"Executable\" projectType=\"cdt.managedbuild.target.gnu.cross.exe\"/>\n" +
                                "	</storageModule>\n" +
                                "	<storageModule moduleId=\"org.eclipse.cdt.core.LanguageSettingsProviders\"/>\n" +
                                "	<storageModule moduleId=\"org.eclipse.cdt.make.core.buildtargets\"/>\n" +
                                "	<storageModule moduleId=\"refreshScope\" versionNumber=\"2\">\n" +
                                "		<configuration configurationName=\"Debug\">\n" +
                                "			<resource resourceType=\"PROJECT\" workspacePath=\"/test\"/>\n" +
                                "		</configuration>\n" +
                                "	</storageModule>\n" +
                                "	<storageModule moduleId=\"org.eclipse.cdt.internal.ui.text.commentOwnerProjectMappings\"/>\n" +
                                "	<storageModule moduleId=\"scannerConfiguration\">\n" +
                                "		<autodiscovery enabled=\"true\" problemReportingEnabled=\"true\" selectedProfileId=\"\"/>\n" +
                                "		<scannerConfigBuildInfo instanceId=\"cdt.managedbuild.config.gnu.cross.exe.release.437048583;cdt.managedbuild.config.gnu.cross.exe.release.437048583.;cdt.managedbuild.tool.gnu.cross.cpp.compiler.1000785969;cdt.managedbuild.tool.gnu.cpp.compiler.input.489541947\">\n" +
                                "			<autodiscovery enabled=\"true\" problemReportingEnabled=\"true\" selectedProfileId=\"\"/>\n" +
                                "		</scannerConfigBuildInfo>\n" +
                                "		<scannerConfigBuildInfo instanceId=\"cdt.managedbuild.config.gnu.cross.exe.debug.1648944120.201227736;cdt.managedbuild.config.gnu.cross.exe.debug.1648944120.201227736.;cdt.managedbuild.tool.gnu.cross.cpp.compiler.1007176335;cdt.managedbuild.tool.gnu.cpp.compiler.input.66512511\">\n" +
                                "			<autodiscovery enabled=\"true\" problemReportingEnabled=\"true\" selectedProfileId=\"\"/>\n" +
                                "		</scannerConfigBuildInfo>\n" +
                                "		<scannerConfigBuildInfo instanceId=\"cdt.managedbuild.config.gnu.cross.exe.debug.1648944120.201227736;cdt.managedbuild.config.gnu.cross.exe.debug.1648944120.201227736.;cdt.managedbuild.tool.gnu.cross.c.compiler.468849173;cdt.managedbuild.tool.gnu.c.compiler.input.1943078395\">\n" +
                                "			<autodiscovery enabled=\"true\" problemReportingEnabled=\"true\" selectedProfileId=\"\"/>\n" +
                                "		</scannerConfigBuildInfo>\n" +
                                "		<scannerConfigBuildInfo instanceId=\"cdt.managedbuild.config.gnu.cross.exe.debug.1648944120.1204396839.669565605.590937248;cdt.managedbuild.config.gnu.cross.exe.debug.1648944120.1204396839.669565605.590937248.;cdt.managedbuild.tool.gnu.cross.c.compiler.997709178;cdt.managedbuild.tool.gnu.c.compiler.input.1952710377\">\n" +
                                "			<autodiscovery enabled=\"true\" problemReportingEnabled=\"true\" selectedProfileId=\"\"/>\n" +
                                "		</scannerConfigBuildInfo>\n" +
                                "		<scannerConfigBuildInfo instanceId=\"cdt.managedbuild.config.gnu.cross.exe.release.437048583;cdt.managedbuild.config.gnu.cross.exe.release.437048583.;cdt.managedbuild.tool.gnu.cross.c.compiler.908772690;cdt.managedbuild.tool.gnu.c.compiler.input.693755125\">\n" +
                                "			<autodiscovery enabled=\"true\" problemReportingEnabled=\"true\" selectedProfileId=\"\"/>\n" +
                                "		</scannerConfigBuildInfo>\n" +
                                "		<scannerConfigBuildInfo instanceId=\"cdt.managedbuild.config.gnu.cross.exe.debug.1648944120.1204396839.669565605.590937248;cdt.managedbuild.config.gnu.cross.exe.debug.1648944120.1204396839.669565605.590937248.;cdt.managedbuild.tool.gnu.cross.cpp.compiler.448379617;cdt.managedbuild.tool.gnu.cpp.compiler.input.60841263\">\n" +
                                "			<autodiscovery enabled=\"true\" problemReportingEnabled=\"true\" selectedProfileId=\"\"/>\n" +
                                "		</scannerConfigBuildInfo>\n" +
                                "		<scannerConfigBuildInfo instanceId=\"cdt.managedbuild.config.gnu.cross.exe.debug.1648944120;cdt.managedbuild.config.gnu.cross.exe.debug.1648944120.;cdt.managedbuild.tool.gnu.cross.c.compiler.1740708202;cdt.managedbuild.tool.gnu.c.compiler.input.2027022934\">\n" +
                                "			<autodiscovery enabled=\"true\" problemReportingEnabled=\"true\" selectedProfileId=\"\"/>\n" +
                                "		</scannerConfigBuildInfo>\n" +
                                "		<scannerConfigBuildInfo instanceId=\"cdt.managedbuild.config.gnu.cross.exe.debug.1648944120;cdt.managedbuild.config.gnu.cross.exe.debug.1648944120.;cdt.managedbuild.tool.gnu.cross.cpp.compiler.1531969055;cdt.managedbuild.tool.gnu.cpp.compiler.input.1035246757\">\n" +
                                "			<autodiscovery enabled=\"true\" problemReportingEnabled=\"true\" selectedProfileId=\"\"/>\n" +
                                "		</scannerConfigBuildInfo>\n" +
                                "	</storageModule>\n" +
                                "</cproject>\n");
                writer.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

}
