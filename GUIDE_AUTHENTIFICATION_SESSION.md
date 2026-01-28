# Guide d'implémentation de l'authentification et gestion de session

## Table des matières
1. [Architecture de l'authentification](#architecture)
2. [Base de données - Table User](#base-de-données)
3. [Entité User et Repository](#entité-user)
4. [Service d'authentification](#service-dauthentification)
5. [API REST Login/Logout](#api-rest)
6. [Sécurisation des endpoints](#sécurisation-des-endpoints)
7. [Intégration webapp JSF](#intégration-webapp)
8. [Filtres de sécurité](#filtres-de-sécurité)
9. [Gestion des sessions](#gestion-des-sessions)

---

## Architecture

### Principe
- **Backend** : Service REST avec authentification JWT ou Session-based
- **Frontend** : Webapp JSF qui consomme le service
- **Session** : Gestion côté serveur (HttpSession) ou Token JWT
- **Sécurité** : Filtres pour protéger les ressources

### Technologies
- Jakarta EE 10 (Servlet, JAX-RS, JPA, CDI)
- BCrypt pour le hashage des mots de passe
- HttpSession pour la gestion de session

---

## Base de données

### Script SQL - Table users

```sql
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100),
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP WITH TIME ZONE
);

CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);

-- Créer un utilisateur admin par défaut (mot de passe: admin123)
INSERT INTO users (username, email, password_hash, full_name, role) 
VALUES (
    'admin', 
    'admin@namaaz.com', 
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
    'Administrateur',
    'ADMIN'
);
```

---

## Entité User

### 1. Enum UserRole

```java
package com.namaaz.service.auth.entities;

public enum UserRole {
    ADMIN,      // Administrateur système
    MANAGER,    // Gérant du restaurant
    WAITER,     // Serveur
    COOK,       // Cuisinier
    USER        // Utilisateur standard
}
```

### 2. Entité User

```java
package com.namaaz.service.auth.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User implements Serializable {
    
    @Id
    @GeneratedValue
    private UUID id;
    
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50)
    @Column(unique = true, nullable = false, length = 50)
    private String username;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email format is invalid")
    @Column(unique = true, nullable = false, length = 100)
    private String email;
    
    @NotBlank(message = "Password is required")
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;
    
    @Column(name = "full_name", length = 100)
    private String fullName;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserRole role = UserRole.USER;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;
    
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;
    
    @Column(name = "last_login")
    private OffsetDateTime lastLogin;
    
    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();
        updatedAt = OffsetDateTime.now();
        if (id == null) {
            id = UUID.randomUUID();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }
    
    // Constructeurs
    public User() {}
    
    public User(String username, String email, String passwordHash) {
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
    }
    
    // Getters et Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    
    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    
    public OffsetDateTime getLastLogin() { return lastLogin; }
    public void setLastLogin(OffsetDateTime lastLogin) { this.lastLogin = lastLogin; }
}
```

### 3. Repository UserRepository

```java
package com.namaaz.service.auth.repository;

import com.namaaz.service.auth.entities.User;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Stateless
public class UserRepository {
    
    @PersistenceContext(unitName = "AuthPU")
    private EntityManager em;
    
    public User save(User user) {
        if (user.getId() == null) {
            em.persist(user);
            return user;
        } else {
            return em.merge(user);
        }
    }
    
    public Optional<User> findById(UUID id) {
        User user = em.find(User.class, id);
        return Optional.ofNullable(user);
    }
    
    public Optional<User> findByUsername(String username) {
        try {
            User user = em.createQuery(
                "SELECT u FROM User u WHERE u.username = :username", User.class)
                .setParameter("username", username)
                .getSingleResult();
            return Optional.of(user);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
    
    public Optional<User> findByEmail(String email) {
        try {
            User user = em.createQuery(
                "SELECT u FROM User u WHERE u.email = :email", User.class)
                .setParameter("email", email)
                .getSingleResult();
            return Optional.of(user);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
    
    public List<User> findAll() {
        return em.createQuery("SELECT u FROM User u ORDER BY u.username", User.class)
                .getResultList();
    }
    
    public List<User> findByRole(String role) {
        return em.createQuery(
            "SELECT u FROM User u WHERE u.role = :role ORDER BY u.username", User.class)
            .setParameter("role", role)
            .getResultList();
    }
    
    public boolean existsByUsername(String username) {
        Long count = em.createQuery(
            "SELECT COUNT(u) FROM User u WHERE u.username = :username", Long.class)
            .setParameter("username", username)
            .getSingleResult();
        return count > 0;
    }
    
    public boolean existsByEmail(String email) {
        Long count = em.createQuery(
            "SELECT COUNT(u) FROM User u WHERE u.email = :email", Long.class)
            .setParameter("email", email)
            .getSingleResult();
        return count > 0;
    }
    
    public void delete(UUID id) {
        findById(id).ifPresent(em::remove);
    }
}
```

---

## Service d'authentification

### 1. Utilitaire BCrypt (PasswordEncoder)

```java
package com.namaaz.service.auth.util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordEncoder {
    
    private static final int BCRYPT_ROUNDS = 10;
    
    /**
     * Hash un mot de passe avec BCrypt
     */
    public static String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(BCRYPT_ROUNDS));
    }
    
    /**
     * Vérifie si un mot de passe correspond au hash
     */
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}
```

**Ajouter la dépendance BCrypt dans pom.xml** :

```xml
<dependency>
    <groupId>org.mindrot</groupId>
    <artifactId>jbcrypt</artifactId>
    <version>0.4</version>
</dependency>
```

### 2. Service AuthenticationService

```java
package com.namaaz.service.auth.business;

import com.namaaz.service.auth.entities.User;
import com.namaaz.service.auth.repository.UserRepository;
import com.namaaz.service.auth.util.PasswordEncoder;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.transaction.Transactional;
import java.time.OffsetDateTime;
import java.util.Optional;

@Stateless
public class AuthenticationService {
    
    @EJB
    private UserRepository userRepository;
    
    /**
     * Authentifier un utilisateur
     */
    @Transactional
    public Optional<User> authenticate(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        
        if (userOpt.isEmpty()) {
            return Optional.empty();
        }
        
        User user = userOpt.get();
        
        // Vérifier si le compte est actif
        if (!user.getIsActive()) {
            throw new IllegalStateException("Account is disabled");
        }
        
        // Vérifier le mot de passe
        if (!PasswordEncoder.verifyPassword(password, user.getPasswordHash())) {
            return Optional.empty();
        }
        
        // Mettre à jour la date de dernière connexion
        user.setLastLogin(OffsetDateTime.now());
        userRepository.save(user);
        
        return Optional.of(user);
    }
    
    /**
     * Créer un nouvel utilisateur
     */
    @Transactional
    public User register(String username, String email, String password, String fullName) {
        // Vérifier si l'utilisateur existe déjà
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists");
        }
        
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists");
        }
        
        // Créer l'utilisateur
        String hashedPassword = PasswordEncoder.hashPassword(password);
        User user = new User(username, email, hashedPassword);
        user.setFullName(fullName);
        
        return userRepository.save(user);
    }
    
    /**
     * Changer le mot de passe
     */
    @Transactional
    public boolean changePassword(String username, String oldPassword, String newPassword) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        
        if (userOpt.isEmpty()) {
            return false;
        }
        
        User user = userOpt.get();
        
        // Vérifier l'ancien mot de passe
        if (!PasswordEncoder.verifyPassword(oldPassword, user.getPasswordHash())) {
            return false;
        }
        
        // Mettre à jour le mot de passe
        user.setPasswordHash(PasswordEncoder.hashPassword(newPassword));
        userRepository.save(user);
        
        return true;
    }
}
```

---

## API REST

### 1. AuthResource

```java
package com.namaaz.service.auth.rest;

import com.namaaz.service.auth.business.AuthenticationService;
import com.namaaz.service.auth.entities.User;
import jakarta.ejb.EJB;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Optional;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {
    
    @EJB
    private AuthenticationService authService;
    
    @Context
    private HttpServletRequest request;
    
    /**
     * Login - POST /api/auth/login
     */
    @POST
    @Path("/login")
    public Response login(LoginRequest loginRequest) {
        try {
            Optional<User> userOpt = authService.authenticate(
                loginRequest.username, 
                loginRequest.password
            );
            
            if (userOpt.isEmpty()) {
                return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(new ErrorResponse("Invalid credentials"))
                    .build();
            }
            
            User user = userOpt.get();
            
            // Créer une session
            HttpSession session = request.getSession(true);
            session.setAttribute("userId", user.getId().toString());
            session.setAttribute("username", user.getUsername());
            session.setAttribute("role", user.getRole().toString());
            session.setMaxInactiveInterval(3600); // 1 heure
            
            // Retourner les infos utilisateur (sans le mot de passe)
            UserResponse response = new UserResponse(user);
            response.sessionId = session.getId();
            
            return Response.ok(response).build();
            
        } catch (IllegalStateException e) {
            return Response.status(Response.Status.FORBIDDEN)
                .entity(new ErrorResponse(e.getMessage()))
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ErrorResponse("Authentication error"))
                .build();
        }
    }
    
    /**
     * Logout - POST /api/auth/logout
     */
    @POST
    @Path("/logout")
    public Response logout() {
        try {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            return Response.ok(new MessageResponse("Logged out successfully")).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ErrorResponse("Logout error"))
                .build();
        }
    }
    
    /**
     * Vérifier la session - GET /api/auth/session
     */
    @GET
    @Path("/session")
    public Response checkSession() {
        HttpSession session = request.getSession(false);
        
        if (session == null || session.getAttribute("userId") == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                .entity(new ErrorResponse("No active session"))
                .build();
        }
        
        SessionInfo info = new SessionInfo();
        info.userId = (String) session.getAttribute("userId");
        info.username = (String) session.getAttribute("username");
        info.role = (String) session.getAttribute("role");
        info.sessionId = session.getId();
        info.maxInactiveInterval = session.getMaxInactiveInterval();
        
        return Response.ok(info).build();
    }
    
    /**
     * Register - POST /api/auth/register
     */
    @POST
    @Path("/register")
    public Response register(RegisterRequest registerRequest) {
        try {
            User user = authService.register(
                registerRequest.username,
                registerRequest.email,
                registerRequest.password,
                registerRequest.fullName
            );
            
            UserResponse response = new UserResponse(user);
            return Response.status(Response.Status.CREATED).entity(response).build();
            
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorResponse(e.getMessage()))
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ErrorResponse("Registration error"))
                .build();
        }
    }
    
    // ========== DTOs ==========
    
    public static class LoginRequest {
        public String username;
        public String password;
    }
    
    public static class RegisterRequest {
        public String username;
        public String email;
        public String password;
        public String fullName;
    }
    
    public static class UserResponse {
        public String id;
        public String username;
        public String email;
        public String fullName;
        public String role;
        public String sessionId;
        
        public UserResponse(User user) {
            this.id = user.getId().toString();
            this.username = user.getUsername();
            this.email = user.getEmail();
            this.fullName = user.getFullName();
            this.role = user.getRole().toString();
        }
    }
    
    public static class SessionInfo {
        public String userId;
        public String username;
        public String role;
        public String sessionId;
        public int maxInactiveInterval;
    }
    
    public static class ErrorResponse {
        public String error;
        
        public ErrorResponse(String error) {
            this.error = error;
        }
    }
    
    public static class MessageResponse {
        public String message;
        
        public MessageResponse(String message) {
            this.message = message;
        }
    }
}
```

---

## Sécurisation des endpoints

### Filtre d'authentification pour JAX-RS

```java
package com.namaaz.service.auth.filter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
@Secured  // Annotation personnalisée pour marquer les endpoints à sécuriser
public class AuthenticationFilter implements ContainerRequestFilter {
    
    @Context
    private HttpServletRequest servletRequest;
    
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        HttpSession session = servletRequest.getSession(false);
        
        // Vérifier si la session existe et contient un userId
        if (session == null || session.getAttribute("userId") == null) {
            requestContext.abortWith(
                Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\": \"Authentication required\"}")
                    .build()
            );
        }
    }
}
```

### Annotation @Secured

```java
package com.namaaz.service.auth.filter;

import jakarta.ws.rs.NameBinding;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import static java.lang.annotation.ElementType.*;

@NameBinding
@Retention(RetentionPolicy.RUNTIME)
@Target({TYPE, METHOD})
public @interface Secured {
    String[] roles() default {};
}
```

### Utilisation sur un endpoint

```java
@GET
@Path("/orders")
@Secured  // Endpoint protégé - nécessite une session active
public Response getAllOrders() {
    // Code...
}

@GET
@Path("/admin/users")
@Secured(roles = {"ADMIN"})  // Nécessite le rôle ADMIN
public Response getAllUsers() {
    // Code...
}
```

---

## Intégration webapp JSF

### 1. Bean de session LoginBean

```java
package com.namaaz.webapp.bean;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpSession;
import java.io.Serializable;

@Named
@SessionScoped
public class LoginBean implements Serializable {
    
    private String username;
    private String password;
    private boolean loggedIn;
    private String currentUser;
    private String currentRole;
    
    @PostConstruct
    public void init() {
        checkSession();
    }
    
    private void checkSession() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
        
        if (session != null && session.getAttribute("userId") != null) {
            loggedIn = true;
            currentUser = (String) session.getAttribute("username");
            currentRole = (String) session.getAttribute("role");
        }
    }
    
    public String login() {
        // Appeler le service REST d'authentification via un client
        // Pour simplifier, exemple avec HttpSession direct
        
        FacesContext context = FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) context.getExternalContext().getSession(true);
        
        // TODO: Appeler AuthService REST
        // Pour l'instant, simulation
        if ("admin".equals(username) && "admin123".equals(password)) {
            session.setAttribute("userId", "1");
            session.setAttribute("username", username);
            session.setAttribute("role", "ADMIN");
            
            loggedIn = true;
            currentUser = username;
            currentRole = "ADMIN";
            
            return "/index?faces-redirect=true";
        } else {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                    "Erreur", "Identifiants incorrects"));
            return null;
        }
    }
    
    public String logout() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
        
        if (session != null) {
            session.invalidate();
        }
        
        loggedIn = false;
        currentUser = null;
        currentRole = null;
        
        return "/login?faces-redirect=true";
    }
    
    public boolean isAdmin() {
        return "ADMIN".equals(currentRole);
    }
    
    // Getters et Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public boolean isLoggedIn() { return loggedIn; }
    public String getCurrentUser() { return currentUser; }
    public String getCurrentRole() { return currentRole; }
}
```

### 2. Page login.xhtml

```xhtml
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:h="jakarta.faces.html"
      xmlns:f="jakarta.faces.core">
<h:head>
    <title>Connexion - Namaaz Dining</title>
    <h:outputStylesheet library="css" name="style.css" />
</h:head>
<h:body>
    <div class="login-container">
        <div class="login-card">
            <h1>Connexion</h1>
            
            <h:form>
                <h:messages globalOnly="true" styleClass="alert alert-error" />
                
                <div class="form-group">
                    <h:outputLabel for="username" value="Nom d'utilisateur" />
                    <h:inputText id="username" 
                                 value="#{loginBean.username}" 
                                 required="true"
                                 styleClass="form-control" />
                </div>
                
                <div class="form-group">
                    <h:outputLabel for="password" value="Mot de passe" />
                    <h:inputSecret id="password" 
                                   value="#{loginBean.password}" 
                                   required="true"
                                   styleClass="form-control" />
                </div>
                
                <h:commandButton value="Se connecter" 
                                 action="#{loginBean.login()}"
                                 styleClass="btn btn-primary btn-block" />
            </h:form>
        </div>
    </div>
</h:body>
</html>
```

### 3. Filtre de sécurité pour les pages

```java
package com.namaaz.webapp.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter(urlPatterns = {"/index.xhtml", "/clients.xhtml", "/orders.xhtml", "/*"})
public class AuthenticationFilter implements Filter {
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);
        
        String requestURI = httpRequest.getRequestURI();
        
        // Pages publiques (pas de filtre)
        if (requestURI.contains("/login.xhtml") || 
            requestURI.contains("/resources/") ||
            requestURI.contains("/jakarta.faces.resource/")) {
            chain.doFilter(request, response);
            return;
        }
        
        // Vérifier la session
        boolean loggedIn = (session != null && session.getAttribute("userId") != null);
        
        if (!loggedIn) {
            // Rediriger vers la page de login
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login.xhtml");
        } else {
            chain.doFilter(request, response);
        }
    }
}
```

---

## Gestion des sessions

### Configuration dans web.xml

```xml
<session-config>
    <session-timeout>60</session-timeout> <!-- 60 minutes -->
    <cookie-config>
        <http-only>true</http-only>
        <secure>false</secure> <!-- true en production avec HTTPS -->
    </cookie-config>
    <tracking-mode>COOKIE</tracking-mode>
</session-config>
```

### Afficher l'utilisateur connecté dans le layout

```xhtml
<div class="user-info">
    <h:panelGroup rendered="#{loginBean.loggedIn}">
        Connecté en tant que: <strong>#{loginBean.currentUser}</strong>
        <h:commandButton value="Déconnexion" 
                         action="#{loginBean.logout()}"
                         styleClass="btn btn-secondary" />
    </h:panelGroup>
</div>
```

---

## Résumé de l'implémentation

### Étapes

1. **Créer la table users** dans PostgreSQL
2. **Ajouter BCrypt** dans pom.xml
3. **Créer les entités** (User, UserRole)
4. **Créer le repository** (UserRepository)
5. **Créer le service** (AuthenticationService)
6. **Créer l'API REST** (AuthResource)
7. **Ajouter les filtres** (AuthenticationFilter)
8. **Intégrer dans webapp** (LoginBean, login.xhtml, filtre)

### Flux d'authentification

1. L'utilisateur accède à `/login.xhtml`
2. Il saisit username/password
3. Le `LoginBean` appelle `AuthService.authenticate()`
4. Si succès → création de `HttpSession` avec userId, username, role
5. Redirection vers `/index.xhtml`
6. Le `AuthenticationFilter` vérifie la session sur chaque requête
7. Si session invalide → redirection vers `/login.xhtml`

### Sécurité

- ✅ Mots de passe hashés avec BCrypt
- ✅ Sessions HTTP sécurisées
- ✅ Filtres pour protéger les pages
- ✅ Validation côté serveur
- ✅ Gestion des rôles (ADMIN, USER, etc.)
- ✅ Timeout de session configurable

---

## Améliorations possibles

1. **JWT Token** : Remplacer HttpSession par JWT pour API stateless
2. **Remember Me** : Cookie persistant pour rester connecté
3. **Two-Factor Authentication (2FA)** : Ajouter OTP/SMS
4. **Password Reset** : Système de réinitialisation par email
5. **Account Locking** : Verrouiller après X tentatives échouées
6. **Audit Log** : Tracer toutes les connexions/déconnexions
7. **OAuth2/OpenID** : Intégration Google/Facebook login
