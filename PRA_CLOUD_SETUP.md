# PRA Cloud API Setup Guide

This guide explains how to configure the POS system to use PRA Cloud API instead of the local IMS service.

## Overview

The system supports three modes for PRA integration:
- **IMS Mode** (default): Uses local IMS service at `http://localhost:8524`
- **Cloud Mode**: Uses PRA Cloud API directly (no IMS installation needed)
- **Stub Mode**: Uses mock/stub for testing

## Configuration

### 1. Switch to Cloud Mode

Edit `backend/src/main/resources/application.yml`:

```yaml
pra:
  mode: cloud  # Change from 'ims' to 'cloud'
```

### 2. Choose Environment

#### For Testing (Sandbox)
```yaml
pra:
  mode: cloud
  cloud:
    environment: sandbox
```

The sandbox token is already configured in `application.yml`.

#### For Production
```yaml
pra:
  mode: cloud
  cloud:
    environment: production
    production-token: YOUR_ACTUAL_TOKEN_HERE
```

**Important:** For production, set the token via environment variable (more secure):

```bash
export PRA_PRODUCTION_TOKEN=your-actual-token-here
```

Or in Windows:
```cmd
set PRA_PRODUCTION_TOKEN=your-actual-token-here
```

### 3. Get Production Token

1. Login to PRA Portal: https://reg.pra.punjab.gov.pk/
2. Navigate to: Registration → POS Client Registration → POS Details
3. Find your POS ID (189278)
4. Copy the production token displayed

### 4. IP Whitelisting (Production Only)

Before using production, email PRA to whitelist your server IP:

**To:** eims@pra.punjab.gov.pk  
**Subject:** IP WhiteList Request | PNTN [YOUR_PNTN] - POS ID 189278

**Body:**
```
Dear PRA Team,

Kindly whitelist the following IP for eIMS integration:

PNTN: [YOUR_PNTN]
BUSINESS NAME: [YOUR_BUSINESS_NAME]
POS ID: 189278
SERVER IP: [YOUR_SERVER_IP]
SERVER LOCATION: [e.g., Pakistan, USA, etc.]

Thank you.
```

## Configuration Options

### Full Cloud Configuration

```yaml
pra:
  mode: cloud
  cloud:
    environment: sandbox  # or production
    sandbox-url: https://ims.pral.com.pk/ims/sandbox/api/Live/PostData
    sandbox-token: 24d8fab3-f2e9-398f-ae17-b387125ec4a2
    production-url: https://ims.pral.com.pk/ims/production/api/Live/PostData
    production-token: ${PRA_PRODUCTION_TOKEN:your-production-token-here}
    pos-id: 189278
    payment-mode: 1
    invoice-type: 1
    default-pct-code: "98211000"
    cash-gst-rate: 0.16
    card-gst-rate: 0.05
```

### Switching Back to IMS

To switch back to local IMS:

```yaml
pra:
  mode: ims
```

## Testing

### 1. Start the Backend
```bash
cd backend
./gradlew bootRun
```

### 2. Check Logs
Look for:
```
Initializing PRA Fiscalization Client with mode: cloud
Using Cloud PRA Fiscalization Client (environment: sandbox)
```

### 3. Test an Invoice
Use the POS frontend to create a test order and complete payment.

### 4. Verify Response
Check backend logs for:
```
PRA Cloud fiscalize success -> Invoice: [INVOICE_NUMBER], Code: 100
```

## Troubleshooting

### Error: "Cloud API token not configured"
**Solution:** Set the token in `application.yml` or via environment variable.

### Error: "PRA Cloud API error (403)"
**Solution:** 
- For production: Ensure your IP is whitelisted (email PRA)
- Verify the token is correct

### Error: "SSL/TLS error"
**Solution:** Ensure Java supports TLS 1.2+. Update Java if needed.

### Error: "Connection timeout"
**Solution:** 
- Check internet connection
- Verify firewall allows HTTPS to `ims.pral.com.pk`

## Benefits of Cloud Mode

✅ No IMS installation required  
✅ No localhost dependency  
✅ Easier deployment  
✅ Direct connection to PRA  
✅ Automatic updates by PRA  
✅ Better reliability  

## Security Notes

1. **Never commit production tokens to Git**
2. Use environment variables for production tokens
3. Keep tokens secure and rotate regularly
4. Use sandbox for development/testing
5. Only use production for live transactions

## API Endpoints

### Sandbox
- **URL:** https://ims.pral.com.pk/ims/sandbox/api/Live/PostData
- **Token:** 24d8fab3-f2e9-398f-ae17-b387125ec4a2
- **Use:** Testing and development

### Production
- **URL:** https://ims.pral.com.pk/ims/production/api/Live/PostData
- **Token:** Get from PRA Portal
- **Use:** Live transactions only
- **Requires:** IP whitelisting

## Support

For issues with PRA Cloud API:
- **Email:** eims@pra.punjab.gov.pk
- **Phone:** 042-99205710

Include in your email:
- PNTN
- POS ID (189278)
- Error screenshot
- Contact number
