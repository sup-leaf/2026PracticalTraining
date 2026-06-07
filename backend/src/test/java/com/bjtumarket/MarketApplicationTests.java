package com.bjtumarket;

import com.bjtumarket.controller.*;
import com.bjtumarket.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * 应用上下文加载集成测试。
 * 验证 Spring 容器能够正确加载所有 Bean，H2 内存数据库初始化正常。
 */
@SpringBootTest
@ActiveProfiles("test")
class MarketApplicationTests {

    @Autowired
    private AuthController authController;
    @Autowired
    private JobController jobController;
    @Autowired
    private DeliveryController deliveryController;
    @Autowired
    private InternshipController internshipController;
    @Autowired
    private CompetitionController competitionController;
    @Autowired
    private ResearchProjectController researchProjectController;
    @Autowired
    private AdminController adminController;
    @Autowired
    private ResumeController resumeController;
    @Autowired
    private ProfileController profileController;
    @Autowired
    private NotificationController notificationController;
    @Autowired
    private UserService userService;
    @Autowired
    private JobService jobService;
    @Autowired
    private AdminService adminService;

    @Test
    void contextLoads() {
        // 验证所有核心 Controller 被成功加载
        assertNotNull(authController);
        assertNotNull(jobController);
        assertNotNull(deliveryController);
        assertNotNull(internshipController);
        assertNotNull(competitionController);
        assertNotNull(researchProjectController);
        assertNotNull(adminController);
        assertNotNull(resumeController);
        assertNotNull(profileController);
        assertNotNull(notificationController);
    }

    @Test
    void allServicesLoad() {
        // 验证所有核心 Service 被成功加载
        assertNotNull(userService);
        assertNotNull(jobService);
        assertNotNull(adminService);
    }
}
