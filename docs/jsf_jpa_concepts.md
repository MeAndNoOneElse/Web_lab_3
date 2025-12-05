# Полный обзор: JavaServer Faces (JSF), ORM, JPA, JDBC

> _Развернутый конспект по всем заданным темам, с дополнительными подробностями и схемами_

---

## 1. **JavaServer Faces (JSF)**

### 1.1 Основы JSF: назначение и отличия

**JavaServer Faces (JSF)** — это спецификация Java для построения component-based web-приложений. JSF предоставляет структурированный подход к разработке UI для серверных веб-приложений.

#### **Главные особенности:**
- **Компонентно-ориентированная архитектура**.
- UI дерево рендерится и управляется на сервере.
- Жизненный цикл обработки событий.
- Богатая поддержка шаблонов, подключаемых библиотек компонентов, валидации, конвертации данных.

#### **Отличия JSF от Servlets и JSP:**
- **Сервлеты** — программная модель для обработки HTTP-запросов/ответов, но управление HTML-разметкой осуществляется вручную в Java-коде.
- **JSP** — «шаблонная» технология для смешивания HTML и Java, но сама логика управления компонентами отсутствует.
- **JSF** — предлагает декларативный, событийно-ориентированный, компонентный UI-подход.

#### **Архитектурная схема:**
```
  +---------------------+
  |  HTTP Клиент        |
  +---------------------+
            |
            v
  +---------------------+
  |  FacesServlet (FrontController)         |
  +---------------------+
            |
            v
  +---------------------+
  | JSF Lifecycle (обработка фаз)           |
  +---------------------+
            |
            v
  +---------------------+
  | Rendered View (UI-компоненты)           |
  +---------------------+
```

**Сервлеты и JSP VS JSF (таблица):**

| Критерий           | Сервлеты         | JSP         | JSF                |
|--------------------|------------------|-------------|--------------------|
| Архитектура        | Императивная     | Декларативная | Компонентная      |
| UI Связывание      | Через код        | Через EL     | UI-компоненты      |
| Жизненный цикл     | Обработка запроса | Шаблонный    | Многофазный        |
| Расширяемость      | Средняя          | Ограничено   | Широкая            |

---

### 1.2 Преимущества и недостатки JSF

#### **Преимущества:**
- Стандартизованный компонентный UI для Java EE.
- Интеграция с CDI, EJB и другими Java EE стандартами.
- Богатый стек дополнительных компонентов (PrimeFaces и др).
- Жизненный цикл и events, понятная модель MVC.

#### **Недостатки:**
- Сложность конфигурирования для новичков.
- Сравнительно низкая производительность на очень нагруженных проектах.
- Ограничения по интерактивности, если не интегрировать с AJAX.
- Большой XML/Java boilerplate в старых версиях.

---

### 1.3 Структура JSF-приложения

Стандартная структура:
```
MyJSFApp/
├── src/
│   └── main/
│       └── java/
│           └── ... (бэкенд-классы, бины)
├── WebContent/
│   ├── WEB-INF/
│   │   ├── faces-config.xml
│   │   └── web.xml
│   ├── index.xhtml
│   ├── template.xhtml
│   └── ... (страницы и шаблоны)
```

- **faces-config.xml**: настройка JSF-артфактов (навигация, бины, и др).
- **web.xml**: конфигурация сервлетов и фильтров (обязательно для FacesServlet).
- **XHTML/JSP**: страницы интерфейса.

---

## 2. **JSP-страницы и Facelets-шаблоны в JSF**

### **JSP vs Facelets**

| Характеристика       | JSP            | Facelets (XHTML)   |
|----------------------|----------------|--------------------|
| Поддержка JSF 2+     | Устарело       | Стандарт           |
| Интеграция AJAX      | Ограничена     | Полная             |
| Рекомпоновка шаблонов| Сложно         | Простая            |

- **Facelets** — рекомендуемый способ разметки для JSF 2.x+, поддерживает шаблоны (`<ui:composition>`, `<ui:insert>`).
- **JSP** — поддерживалось в JSF 1.x, снят с поддержки.

**Пример шаблона на Facelets:**
```xhtml
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:ui="http://java.sun.com/jsf/facelets">
  <h:body>
    <ui:insert name="content"/>
  </h:body>
</html>
```

---

## 3. JSF-компоненты: особенности реализации, иерархия классов

- Каждый UI-элемент — экземпляр класса-наследника `javax.faces.component.UIComponent`.
- Компоненты связаны с renderer'ами, определяющими вывод в HTML.
- Расширение возможно через кастомные компоненты.

**Классическая иерархия:**
```
javax.faces.component.UIComponent
│
├─ UIInput
│   └─ HtmlInputText, HtmlInputSecret, ...
├─ UICommand
│   └─ HtmlCommandButton, HtmlCommandLink
├─ UIOutput
│   └─ HtmlOutputText
└─ ...
```

---

## 4. Дополнительные библиотеки JSF-компонентов

Некоторые популярные библиотеки:
- **PrimeFaces** — лидирующая библиотека с сотнями компонентов (datagrid, charts, file upload).
- **RichFaces**, **ICEfaces** — устаревшие, но встречаются.
- **OmniFaces** — коллекция утилит и дополнительных JSF-компонентов.

---

## 5. Модель обработки событий в JSF

Жизненный цикл JSF обработает события на каждом этапе:
1. **Restore View** — восстановление дерева компонентов.
2. **Apply Request Values** — доставка user input в компоненты.
3. **Process Validations** — запуск валидаторов.
4. **Update Model Values** — обновление бинов.
5. **Invoke Application** — выполнение action-методов.
6. **Render Response** — рендер страницы.

**Схема жизненного цикла:**
```
[Запрос]
   ↓
Restore View
   ↓
Apply Request Values
   ↓
Process Validations
   ↓
Update Model Values
   ↓
Invoke Application
   ↓
Render Response
   ↓
[Ответ]
```

---

## 6. Конвертеры и валидаторы данных JSF

- **Валидаторы** проверяют вход (например, email, диапазон, регулярка).
- **Конвертеры** преобразуют типы данных, например, строка ⇄ дата.

**Пример валидации:**
```xml
<h:inputText value="#{bean.age}">
  <f:validateLongRange minimum="18" maximum="99"/>
</h:inputText>
```
**Custom converter реализуется через implements `javax.faces.convert.Converter`.**

---

## 7. Представление страницы JSF на сервере. Класс `UIViewRoot`

- Каждая страница — дерево компонентов.
- **`UIViewRoot`** — корень дерева компонентов.
- Управляет состоянием страницы, событиями, рендерингом.
- Позволяет программно обращаться к внутренности UI дерева.

---

## 8. Управляемые бины JSF (Managed Beans)

- **Назначение:** Хранение данных формы, обработка бизнес-логики, мост между UI и backend.
- **Конфигурация:**
    - Аннотации (`@ManagedBean`, `@Named` + CDI)
    - В XML (`faces-config.xml` для JSF<2.0)
- **Scope**: `@RequestScoped`, `@ViewScoped`, `@SessionScoped`, `@ApplicationScoped`
- **Контекст:** bean с определённым scope автоматически "видим" в JSF-страницах.

**Пример:**
```java
@ManagedBean
@SessionScoped
public class UserBean {
  private String username;
  // getters/setters
}
```

---

## 9. Конфигурация JSF-приложений

### **faces-config.xml** — основной файл конфигурации:

- Объявляются бины, навигация, конвертеры, валидаторы, слушатели (listeners).
- В современных реализациях аннотации заменяют многое из XML.

### **FacesServlet**
- Регистрируется в `web.xml`:
```xml
<servlet>
  <servlet-name>Faces Servlet</servlet-name>
  <servlet-class>javax.faces.webapp.FacesServlet</servlet-class>
  <load-on-startup>1</load-on-startup>
</servlet>
<servlet-mapping>
  <servlet-name>Faces Servlet</servlet-name>
  <url-pattern>*.xhtml</url-pattern>
</servlet-mapping>
```
- Front Controller для всех JSF-запросов.

---

## 10. Навигация в JSF

- Использование return value из action-метода для перехода между страницами (`return "success";`).
- Навигация определяется в faces-config.xml или через аннотации/action outcomes.
- Поддерживаются view parameters, implicit navigation, redirect (redirect="true").

---

## 11. Работа с БД: JDBC

### **Доступ к БД из Java-приложения:**
- Используется API `java.sql.*`.
- Основные классы: `DriverManager`, `Connection`, `Statement`/`PreparedStatement`, `ResultSet`.

```java
String url = "jdbc:mysql://localhost:3306/db";
Connection conn = DriverManager.getConnection(url, "user", "pwd");
PreparedStatement ps = conn.prepareStatement("SELECT * FROM users WHERE id=?");
ps.setInt(1, 123);
ResultSet rs = ps.executeQuery();
// ... обрабатываем результат
rs.close(); ps.close(); conn.close();
```

- Драйверы: предоставляются производителем СУБД, подключаются через classpath.

---

## 12. ORM: концепция и в Java-приложениях

- **ORM (Object-Relational Mapping)** — технология сопоставления объектных структур Java c реляционными таблицами.
- Позволяет работать с БД через объекты/POJO, а не SQL-запросы.
- Маппинг: аннотации или XML-карты.

**Преимущества:**
- Ускорение разработки.
- Инкапсуляция логики доступа к данным.
- Миграция между БД.

**Недостатки:**
- Возможна потеря контроля над сложным SQL.
- Оверхед по производительности для тяжелых операций.

---

## 13. Основные ORM-библиотеки

- **Hibernate** — самый известный и мощный ORM-фреймворк, поддержка HQL, миграций, кэширования.
- **EclipseLink** — эталонная реализация JPA, интеграция с различными серверами приложений.

| Критерий      | Hibernate          | EclipseLink      |
|---------------|--------------------|------------------|
| Распространенность | Очень высокая  | Средняя          |
| Интеграция с JPA   | Полная         | Эталонный провайдер |
| Особенности        | Много custom-фич, кэш 2-го уровня | Чистая JPA |

---

## 14. JPA — Java Persistence API

- **JPA** — спецификация управления персистентными данными.
- Реализации: Hibernate, EclipseLink, OpenJPA.
- Определяет API для CRUD-операций, кросс-БД portability.

**Ключевые элементы:**
- Аннотации (`@Entity`, `@Table`, `@Id`, `@OneToMany`)
- EntityManager (управление жизненным циклом объекта)
- JPQL (язык запросов высокого уровня)

**Пример минимального Entity:**
```java
@Entity
@Table(name="users")
public class User {
  @Id
  @GeneratedValue
  private Long id;

  private String name;
  // Геттеры/сеттеры
}
```

**Интеграция с драйверами JDBC**:
- JPA-провайдер использует JDBC "под капотом" для доступа к БД.

---

## 15. Важные детали, часто упускаемые

- JSF и JPA тесно интегрируются в стеках Enterprise Java: можно использовать CDI, транзакции через JTA, декларативную безопасность.
- Важно понимать *жизненный цикл JSF* для предотвращения багов с валидацией/инъекцией данных.
- В JSF желательно использовать scope `@ViewScoped` для страниц с динамикой, иначе данные теряются между запросами.
- В JPA всегда рекомендуется явно управлять транзакциями (или через контейнер).
- Использование DTO/VO классов для передачи данных между слоями, чтобы не раскрывать Entity напрямую во View-слой.
- В крупных проектах над JSF часто используют Spring Boot (с модулями JSF-стартеров) или Jakarta EE.
- Всегда используйте connection pool, а не прямое открытие JDBC-соединений.

---

# **Схема связей между технологиями:**
```
[JSF-страница] <-> [Managed Bean] <-> [JPA/DAO слой] <-> [JDBC драйвер] <-> [СУБД]
                                    |
                                   ORM (Hibernate/EclipseLink)
```

---

## **Источники и для углубленного чтения**

- [JSF: Jakarta Faces (официальное руководство)](https://jakarta.ee/specifications/faces/)
- [JPA спецификация (Jakarta Persistence)](https://jakarta.ee/specifications/persistence/)
- [Справочник по Hibernate](https://hibernate.org/)
- [PrimeFaces документация](https://primefaces.org/)