package cn.iocoder.yudao.server.controller;

import cn.hutool.core.io.IoUtil;
import com.alibaba.fastjson.JSONObject;
import jakarta.annotation.security.PermitAll;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@CrossOrigin
@RestController
@RequestMapping("/file")
public class FileTempController {

    /**
     * 接收并读取前端上传的txt文件，并将内容返回为txt文件
     *
     * @param file 前端上传的txt文件
     * @return 包含处理后内容的txt文件
     */
    @PermitAll
    @PostMapping("/uploadTxt")
    public ResponseEntity<byte[]> uploadTxt(@RequestParam("file") MultipartFile file) {
        JSONObject dict = new JSONObject();
        dict.put("荀", "心s 真部合三[ĭwen] 平 諄         [ĭwĕn] 〈相倫〉");
        dict.put("篇", "滂p' 真部開一 [en] 平     山（喉牙）[æn]    仙（舌齒脣）[ĭɛn] 〈芳連〉");
        dict.put("第", "定d 脂部開四[iei] 去 霽 〈特計〉");
        dict.put("修", "心s 幽部開三[iəu] 平 尤         [iəu] 息流");

        // 检查文件是否为空
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("文件为空，请重新上传。".getBytes(StandardCharsets.UTF_8));
        }

        // 检查文件类型是否为txt
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.endsWith(".txt")) {
            return ResponseEntity.badRequest().body("文件格式错误，请上传txt文件。".getBytes(StandardCharsets.UTF_8));
        }

        try {
            // 使用Hutool读取文件内容，保留空格和换行
            String content = IoUtil.read(file.getInputStream(), StandardCharsets.UTF_8);

            // 替换逻辑：非空格和非换行符字符替换为1
            StringBuilder processedContent = new StringBuilder();
            for (char c : content.toCharArray()) {
                if ( c == ' ' || c == '\n' || c == '\r') {
                    processedContent.append(c);
                } else {
                    Object strObj  = dict.get(String.valueOf(c));
                    if(strObj == null|| strObj.equals("")){
                        processedContent.append(c);
                    }else{
                        processedContent.append(c).append("【").append(strObj).append("】");
                    }
                }
            }

            // 将处理后的内容作为文件返回
            byte[] contentBytes = processedContent.toString().getBytes(StandardCharsets.UTF_8);
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=output.txt");
            headers.add(HttpHeaders.CONTENT_TYPE, "text/plain; charset=UTF-8");

            return new ResponseEntity<>(contentBytes, headers, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(("读取文件内容失败：" + e.getMessage()).getBytes(StandardCharsets.UTF_8));
        }
    }

    @GetMapping("/test")
    public String test() {
        return "Hello, World!";
    }
}