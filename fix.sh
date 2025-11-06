cd "D:\11-3-project\New folder (3) claude caude refactored\syos-pos-system-chirath"

# Create ServiceFactory directory in pos-core
mkdir -p "pos-core/src/main/java/com/syos/infrastructure/factories"

# Create ServiceFactory.java
cat > "pos-core/src/main/java/com/syos/infrastructure/factories/ServiceFactory.java" << 'EOF'
package com.syos.infrastructure.factories;

import com.syos.application.services.*;
import com.syos.infrastructure.persistence.gateways.*;

/**
 * Service Factory - Creates and provides service instances
 * Thread-safe singleton for web application
 */
public class ServiceFactory {
    private static ServiceFactory instance;

    private final SalesService salesService;
    private final InventoryService inventoryService;
    private final ReportService reportService;
    private final UserService userService;

    private ServiceFactory() {
        // Initialize gateways
        BillGateway billGateway = new BillGateway();
        ItemGateway itemGateway = new ItemGateway();
        UserGateway userGateway = new UserGateway();

        // Initialize services
        this.salesService = new SalesService(billGateway, itemGateway);
        this.inventoryService = new InventoryService(itemGateway);
        this.reportService = new ReportService(billGateway, itemGateway);
        this.userService = new UserService(userGateway);
    }

    public static synchronized ServiceFactory getInstance() {
        if (instance == null) {
            instance = new ServiceFactory();
        }
        return instance;
    }

    public SalesService getSalesService() {
        return salesService;
    }

    public InventoryService getInventoryService() {
        return inventoryService;
    }

    public ReportService getReportService() {
        return reportService;
    }

    public UserService getUserService() {
        return userService;
    }
}
EOF

echo "✅ ServiceFactory created"