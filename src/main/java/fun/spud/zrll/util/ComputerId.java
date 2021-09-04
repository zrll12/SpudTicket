package fun.spud.zrll.util;

import org.apache.commons.codec.binary.Hex;

import java.io.File;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ComputerId {
    /**
     * Get the name of your operating.
     *
     * @return The number
     */
    public static String getOSName() {
        return System.getProperty("os.name").toLowerCase();
    }

    /**
     * Get your mainboard serial number(used in Windows).
     *
     * @return The number
     */
    public static String getMainBordId_windows() {
        StringBuilder result = new StringBuilder();
        try {
            File file = File.createTempFile("realhowto", ".vbs");
            file.deleteOnExit();
            FileWriter fw = new java.io.FileWriter(file);

            String vbs = "Set objWMIService = GetObject(\"winmgmts:\\\\.\\root\\cimv2\")\n"
                    + "Set colItems = objWMIService.ExecQuery _ \n"
                    + "   (\"Select * from Win32_BaseBoard\") \n"
                    + "For Each objItem in colItems \n"
                    + "    Wscript.Echo objItem.SerialNumber \n"
                    + "    exit for  ' do the first cpu only! \n" + "Next \n";

            fw.write(vbs);
            fw.close();
            Process p = Runtime.getRuntime().exec(
                    "cscript //NoLogo " + file.getPath());
            BufferedReader input = new BufferedReader(new InputStreamReader(
                    p.getInputStream()));
            String line;
            while ((line = input.readLine()) != null) {
                result.append(line);
            }
            input.close();
        } catch (Exception e) {
            return null;
        }
        return result.toString().trim();
    }

    /**
     * get your mainboard serial number(used in Linux).
     *
     * @return The number
     */
    static String getMainBordId_linux() {

        String result = "";
        String maniBord_cmd = "dmidecode | grep 'Serial Number' | awk '{print $3}' | tail -1";
        Process p;
        try {
            p = Runtime.getRuntime().exec(
                    new String[]{"sh", "-c", maniBord_cmd});
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    p.getInputStream()));
            String line;
            if ((line = br.readLine()) != null) {
                result += line;
            }
            br.close();
        } catch (IOException e) {
            return null;
        }
        return result;
    }

    /**
     * get your mainboard serial number
     *
     * @return The number
     */
    static String getMainBordId() {
        String os = getOSName();
        String mainBordId = "";
        if (os.startsWith("windows")) {
            mainBordId = getMainBordId_windows();
        } else if (os.startsWith("linux")) {
            mainBordId = getMainBordId_linux();
        }
        if (mainBordId == null || mainBordId.equals("")) {
            mainBordId = "null";
        }
        return mainBordId;
    }

    /**
     * Get your cpu serial number(used in Windows).
     *
     * @return The number
     */
    static String getCPUID_Windows() {
        StringBuilder result = new StringBuilder();
        try {
            File file = File.createTempFile("tmp", ".vbs");
            file.deleteOnExit();
            FileWriter fw = new java.io.FileWriter(file);
            String vbs = "Set objWMIService = GetObject(\"winmgmts:\\\\.\\root\\cimv2\")\n"
                    + "Set colItems = objWMIService.ExecQuery _ \n"
                    + "   (\"Select * from Win32_Processor\") \n"
                    + "For Each objItem in colItems \n"
                    + "    Wscript.Echo objItem.ProcessorId \n"
                    + "    exit for  ' do the first cpu only! \n" + "Next \n";

            // + "    exit for  \r\n" + "Next";
            fw.write(vbs);
            fw.close();
            Process p = Runtime.getRuntime().exec(
                    "cscript //NoLogo " + file.getPath());
            BufferedReader input = new BufferedReader(new InputStreamReader(
                    p.getInputStream()));
            String line;
            while ((line = input.readLine()) != null) {
                result.append(line);
            }
            input.close();
            file.delete();
        } catch (Exception e) {
            return null;
        }
        return result.toString().trim();
    }

    /**
     * Get your cpu serial number(used in Linux).
     *
     * @return The number
     */
    static String getCPUID_linux() {
        String result = "";
        String CPU_ID_CMD = "dmidecode";
        BufferedReader bufferedReader;
        Process p;
        try {
            p = Runtime.getRuntime().exec(new String[]{"sh", "-c", CPU_ID_CMD});
            bufferedReader = new BufferedReader(new InputStreamReader(
                    p.getInputStream()));
            String line;
            int index;
            while ((line = bufferedReader.readLine()) != null) {
                index = line.toLowerCase().indexOf("uuid");
                if (index >= 0) {
                    result = line.substring(index + "uuid".length() + 1).trim();
                    break;
                }
            }

        } catch (IOException e) {
            return null;
        }
        return result.trim();
    }

    /**
     * Get your cpu serial number
     *
     * @return The number
     */
    static String getCPUId() {
        String os = getOSName();
        String cpuId = "";
        if (os.startsWith("windows")) {
            cpuId = getCPUID_Windows();
        } else if (os.startsWith("linux")) {
            cpuId = getCPUID_linux();
        }
        if (cpuId == null || cpuId.equals("")) {
            cpuId = "null";
        }
        return cpuId;
    }

    /**
     * Get your unique computer id.
     *
     * @return Your computer id.
     * @throws NoSuchAlgorithmException Your OS dose not support md5.
     */
    public static String getComputerID() throws NoSuchAlgorithmException {
        String mainboardid = getMainBordId();
        String cpuid = getCPUId();
        String preid = mainboardid + cpuid;
        String id;
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(preid.getBytes(StandardCharsets.UTF_8));
        id = String.valueOf(Hex.encodeHex(md.digest()));
        org.bukkit.Bukkit.getLogger().info("Your computer id is: " + id);
        return id;
    }
}