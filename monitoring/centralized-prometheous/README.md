### 1. **Hash the Password Using Bcrypt**

You can generate a bcrypt-hashed password using tools like `htpasswd` or an online generator. Here's how to do it with `htpasswd`:

```bash
htpasswd -nBC 10 promuser
```

This command:
- Generates a bcrypt-hashed password with 10 rounds.
- Outputs the username (`promuser`) and the hashed password.

Example output:

```
promuser:$2y$10$5kG2rJmDQkUeP9qFcs/3YumzFZ4lxUJoz8F84L/G12hjGAG.U8oCm
```

Copy the hashed password (everything after `promuser:`).

---

### 2. **Update the `web-config.yml` File**

Replace the password in the `web-config.yml` file with the bcrypt-hashed version:

#### Updated `web-config.yml`

```yaml
basic_auth_users:
  promuser: "$2y$10$5kG2rJmDQkUeP9qFcs/3YumzFZ4lxUJoz8F84L/G12hjGAG.U8oCm"
```

---

### 3. **Restart Prometheus**

Restart the Prometheus stack to apply the updated configuration:

```bash
docker-compose up -d --force-recreate
```

---

### 4. **Update Agent Configuration (if needed)**

Ensure that the Prometheus agents still use the plain-text password in their `remote_write` configurations. Prometheus will validate this against the bcrypt-hashed password.

Example agent configuration (`prom-agent-1.yml`):

```yaml
remote_write:
  - url: "http://prometheus-central:9090/api/v1/write"
    basic_auth:
      username: "promuser"
      password: "your_password_here" # Replace with the plain-text password
```

---

### 5. **Test Again**

1. Verify that the centralized Prometheus starts without errors.
2. Use `curl` to test basic authentication:

   ```bash
   curl -u promuser:your_password_here http://localhost:9090/api/v1/write
   ```

   You should receive a `405 Method Not Allowed` error, confirming that the authentication is working.

---

This ensures that your Prometheus web config uses a secure bcrypt-hashed password, as required. Let me know if you encounter further issues! 🔧