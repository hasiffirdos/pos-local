# PRA Configuration Quick Reference

This guide provides quick configuration snippets for different PRA integration modes.

## 📋 Configuration File Location

Edit: `backend/src/main/resources/application.yml`

---

## 🔧 Mode 1: IMS (Local Service)

**Use when:** IMS service is installed locally on the machine

```yaml
pra:
  mode: ims
  ims-base-url: http://localhost:8524
  ims:
    pos-id: 189278
    payment-mode: 1
    invoice-type: 1
    default-pct-code: "98211000"
    cash-gst-rate: 0.16
    card-gst-rate: 0.05
```

**Requirements:**
- IMS software installed and running
- Service accessible at port 8524

---

## ☁️ Mode 2: Cloud (Sandbox)

**Use when:** Testing without local IMS installation

```yaml
pra:
  mode: cloud
  cloud:
    environment: sandbox
    sandbox-url: https://ims.pral.com.pk/ims/sandbox/api/Live/PostData
    sandbox-token: 24d8fab3-f2e9-398f-ae17-b387125ec4a2
    pos-id: 189278
    payment-mode: 1
    invoice-type: 1
    default-pct-code: "98211000"
    cash-gst-rate: 0.16
    card-gst-rate: 0.05
```

**Requirements:**
- Internet connection
- No IP whitelisting needed for sandbox

---

## 🚀 Mode 3: Cloud (Production)

**Use when:** Live production environment

```yaml
pra:
  mode: cloud
  cloud:
    environment: production
    production-url: https://ims.pral.com.pk/ims/production/api/Live/PostData
    production-token: ${PRA_PRODUCTION_TOKEN}
    pos-id: 189278
    payment-mode: 1
    invoice-type: 1
    default-pct-code: "98211000"
    cash-gst-rate: 0.16
    card-gst-rate: 0.05
```

**Set token via environment variable:**

Windows:
```cmd
set PRA_PRODUCTION_TOKEN=your-actual-token-here
```

Linux/Mac:
```bash
export PRA_PRODUCTION_TOKEN=your-actual-token-here
```

**Requirements:**
- Production token from PRA Portal
- IP whitelisting (email PRA)
- Internet connection

---

## 🧪 Mode 4: Stub (Testing)

**Use when:** Testing without any PRA connection

```yaml
pra:
  mode: stub
  stub:
    enabled: true
    fail-rate: 0.0
    fail-on-amount-above: 0
```

**Note:** Stub mode generates fake invoice numbers for testing

---

## 🔄 Switching Modes

### To switch from IMS to Cloud:
1. Change `mode: ims` to `mode: cloud`
2. Set `environment: sandbox` or `environment: production`
3. Restart backend

### To switch from Cloud to IMS:
1. Change `mode: cloud` to `mode: ims`
2. Ensure IMS service is running
3. Restart backend

---

## ✅ Verification

After changing configuration:

1. **Restart Backend**
   ```bash
   cd backend
   ./gradlew bootRun
   ```

2. **Check Logs**
   Look for:
   ```
   Initializing PRA Fiscalization Client with mode: [YOUR_MODE]
   ```

3. **Test Health Endpoint**
   ```bash
   curl http://localhost:8080/api/pra/health
   ```

4. **Create Test Order**
   - Use POS frontend
   - Complete a payment
   - Check for fiscal invoice number

---

## 🆘 Common Issues

### Issue: "Cloud API token not configured"
**Solution:** 
- For sandbox: Check token in `application.yml`
- For production: Set `PRA_PRODUCTION_TOKEN` environment variable

### Issue: "IMS unavailable"
**Solution:**
- Check if IMS service is running
- Verify port 8524 is accessible
- Check IMS logs

### Issue: "Connection timeout"
**Solution:**
- Check internet connection (for cloud mode)
- Verify firewall settings
- Check if URL is correct

---

## 📞 Support

**PRA Technical Support:**
- Email: eims@pra.punjab.gov.pk
- Phone: 042-99205710

**When contacting support, include:**
- PNTN
- POS ID: 189278
- Mode being used (IMS/Cloud)
- Error message/screenshot
