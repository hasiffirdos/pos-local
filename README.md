# POS Local - Point of Sale System

A modern Point of Sale (POS) system with PRA (Punjab Revenue Authority) integration for invoice fiscalization.

## Features

- 🛒 Complete POS functionality with item management
- 💳 Cash and Card payment support
- 📊 Sales tracking and reporting
- 🧾 PRA invoice fiscalization with QR codes
- 🔄 Real-time inventory management
- 👥 Admin panel for item and category management

## Tech Stack

### Backend
- Java 17 + Spring Boot 3.2.5
- SQLite database
- Flyway migrations
- REST API

### Frontend
- Next.js 14
- React 18
- TypeScript
- Tailwind CSS

## Quick Start

### Prerequisites
- Java 17 or higher
- Node.js 18 or higher
- Git (for automatic updates)

### Running the Application

#### Windows
Simply double-click `run-all.bat` or run:
```cmd
run-all.bat
```

This will:
1. Check if Git is installed
2. Pull latest changes from repository
3. Start backend (port 8080)
4. Start frontend (port 3000)

#### Manual Start

**Backend:**
```bash
cd backend
./gradlew bootRun
```

**Frontend:**
```bash
cd frontend
npm install
npm run dev
```

## PRA Integration Modes

The system supports three modes for PRA integration:

### 1. IMS Mode (Default)
Uses local IMS service installed on the machine.
```yaml
pra:
  mode: ims
```

### 2. Cloud Mode
Direct connection to PRA Cloud API (no IMS installation needed).
```yaml
pra:
  mode: cloud
```

**📖 See [PRA_CLOUD_SETUP.md](PRA_CLOUD_SETUP.md) for detailed cloud setup instructions.**

### 3. Stub Mode
Mock mode for testing without PRA connection.
```yaml
pra:
  mode: stub
```

## Configuration

### Backend Configuration
Edit `backend/src/main/resources/application.yml`:

```yaml
server:
  port: 8080

pra:
  mode: ims  # Options: ims, cloud, stub
  ims:
    pos-id: 189278
    default-pct-code: "98211000"
```

### Frontend Configuration
Create `frontend/.env.local`:

```env
NEXT_PUBLIC_API_URL=http://localhost:8080
NEXT_PUBLIC_POS_ID=189278
```

## Project Structure

```
pos-local/
├── backend/                 # Spring Boot backend
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/       # Java source code
│   │   │   └── resources/  # Configuration & migrations
│   │   └── test/           # Tests
│   └── build.gradle        # Dependencies
├── frontend/               # Next.js frontend
│   ├── app/               # App router pages
│   ├── src/               # Components & utilities
│   └── package.json       # Dependencies
├── run-all.bat            # Windows startup script
├── run-backend.bat        # Backend only
├── run-frontend.bat       # Frontend only
└── README.md              # This file
```

## API Endpoints

### Items
- `GET /api/items` - List all items
- `POST /api/items` - Create item
- `PUT /api/items/{id}` - Update item
- `DELETE /api/items/{id}` - Delete item

### Orders
- `POST /api/orders` - Create order
- `GET /api/orders` - List orders
- `GET /api/orders/{id}` - Get order details

### PRA
- `GET /api/pra/health` - Check PRA connection status

## Database

The application uses SQLite with the following schema:
- `items` - Product catalog
- `orders` - Order records
- `order_items` - Order line items

Database file: `backend/data/pos.db`

## Development

### Adding New Items
1. Go to Admin Panel: http://localhost:3000/admin/items
2. Click "Add New Item"
3. Fill in details including PCT Code
4. Save

### Testing PRA Integration
1. Create a test order in POS
2. Complete payment
3. Check logs for fiscalization status
4. Verify QR code on receipt

## Troubleshooting

### Backend won't start
- Check if port 8080 is available
- Verify Java 17+ is installed: `java -version`
- Check logs in backend terminal

### Frontend won't start
- Check if port 3000 is available
- Verify Node.js is installed: `node -v`
- Run `npm install` in frontend folder

### PRA Connection Issues
- **IMS Mode:** Ensure IMS service is running at port 8524
- **Cloud Mode:** Check internet connection and token configuration
- See [PRA_CLOUD_SETUP.md](PRA_CLOUD_SETUP.md) for cloud troubleshooting

### Git Pull Fails
- Check internet connection
- Verify repository access
- Manually run: `git pull origin main`

## Support

For PRA-related issues:
- **Email:** eims@pra.punjab.gov.pk
- **Phone:** 042-99205710

## License

Proprietary - All rights reserved
