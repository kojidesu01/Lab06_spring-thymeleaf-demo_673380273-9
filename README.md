# Lab 06: Custom ViewResolver ใน Spring Boot + Thymeleaf

**รายวิชา:** CP353002 Principles of Software Design [1]  
**ผู้จัดทำ:** นายธีรเมธ สายคำ (Teeramet Saikham)  
**รหัสนักศึกษา:** 673380273-9  
**วิทยาลัยการคอมพิวเตอร์ มหาวิทยาลัยขอนแก่น**

---

## 1. วัตถุประสงค์ของแล็บ (Objectives)
การทดลองนี้มีวัตถุประสงค์เพื่อศึกษาและทำความเข้าใจกลไกการทำงานของ **ViewResolver** ใน Spring Boot ร่วมกับ **Thymeleaf Template Engine** โดยมุ่งเน้นการปรับตั้งค่า (Configuration) เพื่อย้ายโฟลเดอร์เก็บเทมเพลตจากค่าเริ่มต้นปกติ (`/src/main/resources/templates/`) ไปยังโฟลเดอร์ที่เรากำหนดขึ้นเองคือ `/src/main/resources/my-templates/` เพื่อเรียนรู้เรื่องการออกแบบซอฟต์แวร์ตามหลักการ MVC [2]

---

## 2. หลักการออกแบบซอฟต์แวร์ที่เกี่ยวข้อง (Software Design Principles)
การแยก ViewResolver ออกจาก Controller สอดคล้องกับสถาปัตยกรรมระดับสากล 2 ประการหลัก ได้แก่: [3]

*   **Separation of Concerns (การแยกส่วนหน้าที่รับผิดชอบ):**
    *   **Controller:** มีหน้าที่รับผิดชอบเพียงการควบคุมการไหลของโปรแกรม (Application Logic) จัดการ Request และจัดเตรียม Model ข้อมูลเท่านั้น โดยไม่ต้องรับรู้ว่าไฟล์ HTML ถูกจัดเก็บไว้ที่ตำแหน่งใดทางกายภาพ (Physical Path) บน Disk [3]
    *   **ViewResolver:** ทำหน้าที่เฉพาะในการแปลงชื่อ View เชิงตรรกะ (Logical View Name) ให้กลายเป็นตำแหน่งของไฟล์ Template จริง ๆ และดำเนินการเรนเดอร์ร่วมกับข้อมูลจาก Model [3]
*   **Dependency Inversion Principle (DIP):**
    *   Controller ไม่ขึ้นตรงต่อรายละเอียดการจัดเก็บของ View แต่มันขึ้นตรงต่อ "นามธรรม" (Abstraction) ซึ่งก็คือชื่อเชิงตรรกะที่เป็นเพียง String เปล่า ๆ (เช่น `"home"` หรือ `"about"`) [3, 4]
    *   การทำเช่นนี้ทำให้เราสามารถโยกย้ายโฟลเดอร์ ย้ายระบบไฟล์ หรือกระทั่งเปลี่ยน Template Engine จาก Thymeleaf ไปเป็นอย่างอื่นได้ง่าย ๆ ผ่านการแก้ไฟล์ Configuration (`ThymeleafConfig.java`) โดยไม่มีผลกระทบต่อโค้ดเดิมใน Controller แม้แต่บรรทัดเดียว [3]

---

## 3. โครงสร้างโปรเจกต์ (Project Structure)
โครงสร้างไดเรกทอรีของโปรเจกต์นี้ได้รับการปรับปรุงเพื่อรองรับโฟลเดอร์เทมเพลตเฉพาะตัวและฟังก์ชันใหม่เพิ่มเติม ดังนี้: [4, 5]

```text
spring-thymeleaf-demo/
 ├── pom.xml                                   <- ไฟล์ควบคุม Dependency (Spring Web, Thymeleaf)
 ├── .gitignore
 └── src/main/
      ├── java/com/example/demo/
      │    ├── DemoApplication.java             <- คลาสหลักสำหรับรัน Spring Boot
      │    ├── config/
      │    │    └── ThymeleafConfig.java       <- กำหนด Custom ViewResolver ชี้ไปยัง /my-templates/
      │    └── controller/
      │         └── HomeController.java         <- จัดการ Routing สำหรับหน้า "/" และ "/about"
      └── resources/
           ├── application.properties           <- กำหนดพอร์ตเซิร์ฟเวอร์เป็น 9090
           └── my-templates/                    <- โฟลเดอร์เก็บเทมเพลต HTML ที่กำหนดเอง
                ├── home.html                   <- หน้าหลักแสดงชื่อและรหัสนักศึกษา
                ├── about.html                  <- หน้าแสดงประวัติและคำแนะนำตัวสั้น ๆ
                └── error.html                  <- หน้ารองรับข้อผิดพลาดเมื่อระบบรันไม่ผ่าน
```

---

## 4. โค้ดส่วนที่สำคัญ (Core Implementations)

### 4.1 Custom ViewResolver Config (`ThymeleafConfig.java`)
```java
package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;

@Configuration
public class ThymeleafConfig {

    @Bean
    public SpringResourceTemplateResolver templateResolver() {
        SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
        resolver.setPrefix("classpath:/my-templates/"); // ย้ายมาใช้ my-templates
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML");
        resolver.setCharacterEncoding("UTF-8");
        resolver.setCacheable(false); // ปิดแคชเพื่อให้แก้ไขโค้ด HTML แล้วเห็นผลลัพธ์ทันทีขณะทดสอบ
        return resolver;
    }

    @Bean
    public SpringTemplateEngine templateEngine(SpringResourceTemplateResolver templateResolver) {
        SpringTemplateEngine engine = new SpringTemplateEngine();
        engine.setTemplateResolver(templateResolver);
        return engine;
    }

    @Bean
    public ThymeleafViewResolver viewResolver(SpringTemplateEngine templateEngine) {
        ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
        viewResolver.setTemplateEngine(templateEngine);
        viewResolver.setCharacterEncoding("UTF-8");
        viewResolver.setOrder(1); // กำหนดลำดับความสำคัญในการตรวจสอบก่อน ViewResolver ตัวอื่น
        return viewResolver;
    }
}
```

### 4.2 Application Controller (`HomeController.java`)
```java
package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("message", "Hello Teeramet Saikham");
        model.addAttribute("studentId", "673380273-9");
        return "home"; // คืนชื่อ view เชิงตรรกะ "home" เพื่อส่งต่อไปแปลงเป็นพาร์ท /my-templates/home.html
    }

    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("message", "ยินดีต้อนรับเข้าสู่หน้าแนะนำตัวของผม!");
        return "about"; // คืนชื่อ view เชิงตรรกะ "about" เพื่อแสดงพาร์ท /my-templates/about.html
    }
}
```

### 4.3 Configurations (`application.properties`)
```properties
server.port=9090
spring.thymeleaf.cache=false
```

---

## 5. วิธีการติดตั้งและเริ่มทำงานของโปรเจกต์ (Installation & Running)

### 5.1 ขั้นเตรียมการก่อนเริ่มต้น (Prerequisites)
*   Java Development Kit (JDK) 17 หรือรุ่นที่ใหม่กว่า [6]
*   Apache Maven [6]

### 5.2 วิธีสั่งรันระบบ
เปิด Terminal ในโฟลเดอร์หลักของโปรเจกต์ และป้อนคำสั่งล้างบิลด์เก่าแล้วรันใหม่เพื่อความสมบูรณ์แบบ:
```bash
mvn clean spring-boot:run
```

เมื่อเห็นเซิร์ฟเวอร์ Tomcat สตาร์ตสำเร็จเรียบร้อยแล้ว สามารถเข้าใช้งานเพื่อตรวจสอบผลงานผ่านเว็บเบราว์เซอร์ได้ที่ที่อยู่ดังต่อไปนี้:
*   **หน้าแรก (แสดงชื่อและรหัส):** [http://localhost:9090/](http://localhost:9090/)
*   **หน้าแนะนำตัว (About):** [http://localhost:9090/about](http://localhost:9090/about)

---
*เอกสารนี้จัดทำขึ้นเป็นส่วนหนึ่งของแล็บการเรียนรู้วิชา CP353002 Principles of Software Design* [7]
