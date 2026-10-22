package com.rookie.rookiemeeting.controller;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.baidu.aip.face.AipFace;
import com.rookie.rookiemeeting.common.lang.Constants;
import com.rookie.rookiemeeting.common.lang.Result;
import com.rookie.rookiemeeting.dto.LoginDto;
import com.rookie.rookiemeeting.entity.Employee;
import com.rookie.rookiemeeting.service.IEmployeeService;
import com.rookie.rookiemeeting.service.IMeetingParticipantsService;
import com.rookie.rookiemeeting.service.IMenuService;
import com.rookie.rookiemeeting.util.BASE64;
import com.rookie.rookiemeeting.util.TokenUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

@RestController
@Api(tags = "人脸识别控制类")
@Slf4j
public class PhotoController {

    @Autowired
    IEmployeeService employeeService;
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    IMenuService menuService;
    @Autowired
    IMeetingParticipantsService meetingParticipantsService;
    //人脸信息储存位置
    @Value("${files.upload.path}")
    private String fileUploadPath;
    //临时照片存储位置
    @Value("${photo.tmp}")
    private String tmpPhoto;
    //是否启用百度人脸识别。不启用时为演示模式：可以录入照片，签到不比对人脸，不能刷脸登录
    @Value("${baidu.enabled:false}")
    private boolean baiduEnabled;
    //百度人脸识别接口
    @Autowired
    private AipFace aipFace;

    /**
     * 人脸登录
     *
     * @param file
     * @return
     */
    @ApiOperation("人脸登录接口")
    @PostMapping("/faceLogin")
    public Result faceLogin(@RequestParam("file") MultipartFile file) {
        if (!baiduEnabled) {
            return Result.fail("没有配置百度人脸识别，暂时不能刷脸登录，请用账号密码登录");
        }
        File dest = saveFile(file, tmpPhoto);
        Employee employee = searchFace(dest);
        if (employee == null) {
            return Result.fail("登录失败，没有识别出你的人脸，请重新刷脸或用账号密码登录");
        }
        if (employee.getStatus() == null || employee.getStatus() != 1) {
            return Result.fail("账号还没有通过审批");
        }
        LoginDto loginDto = new LoginDto();
        BeanUtil.copyProperties(employee, loginDto, true);
        String jwt = TokenUtils.genToken(employee.getEmployeeid().toString(), employee.getPassword());
        redisTemplate.opsForValue().set("JWT_" + jwt, JSON.toJSON(employee), 2, TimeUnit.HOURS);
        loginDto.setToken(jwt);
        loginDto.setMenus(menuService.getRoleMenu(employee.getRole()));
        loginDto.setPassword(null);
        return Result.succ(loginDto);
    }

    /**
     * 人脸照片录入
     *
     * @param file
     * @param employeeid
     * @return
     */
    @ApiOperation("人脸照片录入接口")
    @PostMapping("/registered")
    public Result registered(@RequestParam("file") MultipartFile file,
                             @RequestParam("employeeid") Long employeeid) {
        Employee current = TokenUtils.getCurrentUser();
        if (current == null || (!current.getEmployeeid().equals(employeeid) && !Constants.ROLE_ADMIN.equals(current.getRole()))) {
            return Result.fail(Constants.CODE_401, "只能录入自己的人脸照片");
        }
        File dest = saveFile(file, fileUploadPath);

        if (baiduEnabled) {
            // 传入可选参数调用接口
            HashMap<String, String> options = new HashMap<String, String>();
            options.put("user_info", "user's info");
            options.put("quality_control", "NORMAL");
            options.put("liveness_control", "NONE");
            options.put("action_type", "REPLACE");
            // 人脸注册
            JSONObject res = aipFace.addUser(BASE64.encodeImgageToBase64(dest), "BASE64", "employee", employeeid.toString(), options);
            if (res.optInt("error_code", -1) != 0) {
                log.warn("百度人脸注册失败: {}", res);
                return Result.fail("人脸录入失败：" + res.optString("error_msg", "百度接口没有返回结果"));
            }
        }

        Employee employee = new Employee();
        employee.setEmployeeid(employeeid);
        employee.setFaceimg(dest.getName());
        employeeService.updateById(employee);
        return Result.succ(dest.getName());
    }

    /**
     * 签到
     *
     * @param file
     * @param meetingid
     * @param location
     * @return
     */
    @ApiOperation("签到接口")
    @PostMapping("/SingIn")
    public Result SingIn(@RequestParam(value = "file", required = false) MultipartFile file,
                         @RequestParam Integer meetingid,
                         @RequestParam(defaultValue = "") String location) {
        //签到的人以登录账号为准，不信任前端传来的员工ID
        Employee current = TokenUtils.getCurrentUser();
        if (current == null) {
            return Result.fail(Constants.CODE_401, "请先登录");
        }
        if (baiduEnabled) {
            if (file == null || file.isEmpty()) {
                return Result.fail("请拍照或上传照片");
            }
            Employee employee = searchFace(saveFile(file, tmpPhoto));
            if (employee == null || !employee.getEmployeeid().equals(current.getEmployeeid())) {
                return Result.fail("签到失败，照片和当前账号对不上，请重新拍照");
            }
        }
        meetingParticipantsService.singIn(current.getEmployeeid(), meetingid, location);
        return Result.succ(baiduEnabled ? "签到成功" : "签到成功（演示模式，没有比对人脸）");
    }

    /**
     * 文件下载
     *
     * @param name
     * @param response
     */
    @ApiOperation("文件下载接口")
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) throws IOException {
        //只取文件名，防止用 ../ 读到上传目录以外的文件
        File file = new File(uploadDir(fileUploadPath), new File(String.valueOf(name)).getName());
        if (!file.isFile()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        response.setContentType(name.toLowerCase().endsWith(".png") ? "image/png" : "image/jpeg");
        try (FileInputStream in = new FileInputStream(file); ServletOutputStream out = response.getOutputStream()) {
            byte[] bytes = new byte[8192];
            int len;
            while ((len = in.read(bytes)) != -1) {
                out.write(bytes, 0, len);
            }
        }
    }

    //保存上传的照片，文件名前加上当前时间
    private File saveFile(MultipartFile file, String dir) {
        String original = file.getOriginalFilename() == null ? "photo.jpg" : new File(file.getOriginalFilename()).getName();
        String fileName = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date()) + "." + original;
        //要用绝对路径：相对路径会被 Tomcat 解析到它自己的临时目录下
        File dest = new File(uploadDir(dir), fileName);
        try {
            file.transferTo(dest);
        } catch (IOException e) {
            throw new RuntimeException("上传文件失败,服务器发生异常!", e);
        }
        return dest;
    }

    private File uploadDir(String dir) {
        File folder = new File(dir).getAbsoluteFile();
        if (!folder.exists() && !folder.mkdirs()) {
            throw new RuntimeException("创建目录失败：" + folder);
        }
        return folder;
    }

    //百度人脸搜索 M:N 识别，返回识别出的员工，识别不出返回 null
    private Employee searchFace(File photo) {
        HashMap<String, Object> options = new HashMap<>();
        options.put("max_face_num", "3");
        options.put("match_threshold", "80");
        options.put("quality_control", "NORMAL");
        options.put("liveness_control", "NONE");
        options.put("max_user_num", "3");
        JSONObject res = aipFace.multiSearch(BASE64.encodeImgageToBase64(photo), "BASE64", "employee", options);
        try {
            int id = res.getJSONObject("result").getJSONArray("face_list")
                    .getJSONObject(0).getJSONArray("user_list")
                    .getJSONObject(0).getInt("user_id");
            return employeeService.getById(id);
        } catch (Exception e) {
            log.info("人脸识别没有结果: {}", res);
            return null;
        }
    }
}
