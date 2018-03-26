package com.zhevol.library.net;

/**
 * 网络请求的相关信息类，包括请求的 URL 地址、命名空间、方法名
 *
 * @author Zhevol
 * @date 2018/3/23 0023
 */
public class HttpConfig {

    /**
     * 访问的基础 URL
     */
    public static final String BASE_URL = "http://118.126.108.178/FTCloudService.php";

    /**
     * 访问的命名空间
     */
    public static final String NAME_SPACE = "urn:FtCloudService";

    /**
     * 密码登录的方法
     */
    public static final String PASSWORD_LOGIN = "PasswordLogin";

    /**
     * 发送登录验证码的方法
     */
    public static final String SEND_SMS_FOR_LOGIN = "SendSmsForLogin";

    /**
     * 发送注册验证码的方法
     */
    public static final String SEND_SMS_FOR_REGIST = "SendSmsForRegist";

    /**
     * 注册 APP 用户
     */
    public static final String REGIST_APP_USER = "RegistAppuser";

    /**
     * 根据用户编号查询用户关注信息的方法
     */
    public static final String QUERY_USER_STATE = "QueryUserState";

    /**
     * 根据院校名称模糊查询院校信息的方法
     */
    public static final String QUERY_ACADEMY = "QueryAcademy";

    /**
     * 关注院校的方法
     */
    public static final String BIND_ACADEMY = "BindAcademy";

    /**
     * 取消关注院校的方法
     */
    public static final String UNBIND_ACADEMY = "UnBindAcademy";

    /**
     * 院校首页统计信息查询的方法
     */
    public static final String QUERY_ACADEMY_STATICTICS = "QueryAcademyStatictics";

    /**
     * 根据学籍号查询学生信息的方法
     */
    public static final String QUERY_STUDENT_INFO = "QueryStudentInfo";

    /**
     * 关注学生的方法
     */
    public static final String BIND_STUDENT = "BindStudent";

    /**
     * 取消关注学生的方法
     */
    public static final String UNBIND_STUDENT = "UnBindStudent";

    /**
     * 查询学生体测登记卡的方法
     */
    public static final String QUERY_BIND_STUDNET_DETAIL = "QueryBindStudentDetails";

    /**
     * 查询当前体测进度的方法
     */
    public static final String QUERY_FITNESS_TEST_PROGRESS = "QueryFitnessTestProgress";

    /**
     * 判断 excel 数据是否已经导入系统的方法
     */
    public static final String IS_EXCEL_IMPORT_IN = "IsExcelImportIn";

    /**
     * 查询所有年级的方法
     */
    public static final String QUERY_GRADES = "QueryGrades";

    /**
     * 查询所有班级的方法
     */
    public static final String QUERY_CLASSES = "Queryclasses";

    /**
     * 查询所有需要进行的项目的方法
     */
    public static final String QUERY_PROJECT = "Queryprojectes";

    /**
     * 分页查询所有参加体测的学生的方法
     */
    public static final String QUERY_FITNESS_TEST_STUDENT = "QueryFitnesstestStudents";

    /**
     * 提交体测成绩的方法
     */
    public static final String SIGN_TEST_RESULT = "SignTestResult";

    /**
     * 修改体测成绩的方法
     */
    public static final String MODIFY_TEST_RESULT = "ModifyTestResult";

    /**
     * 分页查询所有参加体测的学生的方法，不带处理缓存版本，可以直接调用这个来获取剩余的信息
     */
    public static final String QUERY_NEXT_PAGE_STUDENT = "QueryNextPagestudents";
}
