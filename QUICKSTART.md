# Quick Start Guide

Get the POS system running in 5 minutes!

## 🚀 Fastest Way to Start

### Windows
```cmd
run-all.bat
```

That's it! The script will:
1. Check for Git
2. Pull latest changes
3. Start backend (port 8080)
4. Start frontend (port 3000)

### Access the Application
- **POS:** http://localhost:3000/pos
- **Admin:** http://localhost:3000/admin/items
- **API:** http://localhost:8080/api

---

## 🎯 Choose Your PRA Mode

### Option 1: Testing Without PRA (Fastest)
Edit `backend/src/main/resources/application.yml`:
```yaml
pra:
  mode: stub
```
✅ No setup needed, generates fake invoice numbers

### Option 2: Cloud Sandbox (Recommended for Testing)
```yaml
pra:
  mode: cloud
  cloud:
    environment: sandbox
```
✅ No IMS installation needed  
✅ Real PRA integration  
✅ Token already configured  

### Option 3: Local IMS (Traditional)
```yaml
pra:
  mode: ims
```
⚠️ Requires IMS software installed and running

### Option 4: Cloud Production (Live)
```yaml
pra:
  mode: cloud
  cloud:
    environment: production
```
⚠️ Requires production token and IP whitelisting

---

## 📝 First Time Setup

### 1. Prerequisites
- **Java 17+** - Check: `java -version`
- **Node.js 18+** - Check: `node -v`
- **Git** - Check: `git --version`

### 2. Clone Repository (if not already)
```bash
git clone <your-repo-url>
cd pos-local
```

### 3. Configure PRA Mode
Choose one of the modes above and edit:
```
backend/src/main/resources/application.yml
```

### 4. Start Application
```bash
# Windows
run-all.bat

# Or manually
cd backend && ./gradlew bootRun
cd frontend && npm install && npm run dev
```

### 5. Test It
1. Open http://localhost:3000/pos
2. Add items to cart
3. Complete payment
4. Check for invoice number

---

## 🎨 What Can You Do?

### POS Screen
- Browse items by category
- Add items to cart
- Adjust quantities
- Choose payment method (Cash/Card)
- Complete payment
- Get fiscal invoice with QR code

### Admin Panel
- Add/Edit/Delete items
- Set prices and categories
- Configure PCT codes
- Manage inventory

---

## 🔍 Troubleshooting

### Backend won't start
```bash
# Check if port 8080 is in use
# Windows
netstat -ano | findstr :8080

# Kill the process or change port in application.yml
```

### Frontend won't start
```bash
# Check if port 3000 is in use
# Windows
netstat -ano | findstr :3000

# Or specify different port
cd frontend
npm run dev -- -p 3001
```

### PRA Connection Error
Check your mode in `application.yml`:
- **stub** - No connection needed
- **cloud** - Check internet connection
- **ims** - Check if IMS service is running

---

## 📚 Learn More

- **Full Documentation:** [README.md](README.md)
- **Cloud Setup:** [PRA_CLOUD_SETUP.md](PRA_CLOUD_SETUP.md)
- **Configuration Guide:** [CONFIGURATION_GUIDE.md](CONFIGURATION_GUIDE.md)
- **Implementation Details:** [IMPLEMENTATION_SUMMARY.md](IMPLEMENTATION_SUMMARY.md)

---

## 🆘 Need Help?

### Common Issues

**Q: "Git is not installed"**  
A: Download from https://git-scm.com/download/win

**Q: "Port already in use"**  
A: Close other applications or change port in config

**Q: "PRA IMS unavailable"**  
A: Switch to `mode: stub` or `mode: cloud` for testing

**Q: "Cloud API token not configured"**  
A: You're in production mode. Switch to sandbox or set production token

### Get Support

**PRA Issues:**
- Email: eims@pra.punjab.gov.pk
- Phone: 042-99205710

**Technical Issues:**
- Check logs in terminal windows
- Review configuration files
- Read documentation

---

## ✨ Pro Tips

1. **Use Stub Mode** for initial development
2. **Use Cloud Sandbox** for integration testing
3. **Keep IMS** as backup option
4. **Use Cloud Production** for deployment

5. **Monitor Logs** - Both backend and frontend terminals show useful info

6. **Test with Small Amounts** - When testing production mode

7. **Backup Database** - Located at `backend/data/pos.db`

---

## 🎉 You're Ready!

The system is now running. Start by:
1. Adding some items in Admin Panel
2. Creating a test order in POS
3. Completing payment
4. Checking the fiscal invoice

Happy coding! 🚀
