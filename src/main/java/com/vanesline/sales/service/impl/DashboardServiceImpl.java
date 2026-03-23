package com.vanesline.sales.service.impl;

import com.vanesline.sales.dto.dashboard.*;
import com.vanesline.sales.dto.product.ProductResponse;
import com.vanesline.sales.entity.Product;
import com.vanesline.sales.entity.User;
import com.vanesline.sales.repository.*;
import com.vanesline.sales.service.contract.DashboardService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;

    public DashboardServiceImpl(UserRepository userRepository,
                                OrderRepository orderRepository,
                                OrderItemRepository orderItemRepository,
                                ProductRepository productRepository,
                                InventoryRepository inventoryRepository) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
    }

    private User getUserByUsername(String username) {
        return userRepository.findByUsernameWithRolesAndPermissions(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
    }

    @Override
    public AdminDashboardResponse getAdminDashboard(String username, String period) {
        User user = getUserByUsername(username);
        UUID shopId = user.getShopId();

        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        OffsetDateTime todayStart = now.toLocalDate().atStartOfDay().atOffset(ZoneOffset.UTC);
        OffsetDateTime todayEnd = todayStart.plusDays(1).minusNanos(1);

        OffsetDateTime thisWeekStart = now.minusDays(now.getDayOfWeek().getValue() - 1).toLocalDate().atStartOfDay().atOffset(ZoneOffset.UTC);
        OffsetDateTime thisWeekEnd = thisWeekStart.plusWeeks(1).minusNanos(1);

        OffsetDateTime thisMonthStart = now.withDayOfMonth(1).toLocalDate().atStartOfDay().atOffset(ZoneOffset.UTC);
        OffsetDateTime thisMonthEnd = thisMonthStart.plusMonths(1).minusNanos(1);

        // Previous periods for growth calculation
        OffsetDateTime yesterdayStart = todayStart.minusDays(1);
        OffsetDateTime yesterdayEnd = todayEnd.minusDays(1);

        OffsetDateTime lastWeekStart = thisWeekStart.minusWeeks(1);
        OffsetDateTime lastWeekEnd = thisWeekEnd.minusWeeks(1);

        OffsetDateTime lastMonthStart = thisMonthStart.minusMonths(1);
        OffsetDateTime lastMonthEnd = thisMonthEnd.minusMonths(1);

        BigDecimal revenueToday = getRevenueOrDefault(shopId, todayStart, todayEnd);
        BigDecimal revenueThisWeek = getRevenueOrDefault(shopId, thisWeekStart, thisWeekEnd);
        BigDecimal revenueThisMonth = getRevenueOrDefault(shopId, thisMonthStart, thisMonthEnd);

        BigDecimal prevRevenueToday = getRevenueOrDefault(shopId, yesterdayStart, yesterdayEnd);
        BigDecimal prevRevenueThisWeek = getRevenueOrDefault(shopId, lastWeekStart, lastWeekEnd);
        BigDecimal prevRevenueThisMonth = getRevenueOrDefault(shopId, lastMonthStart, lastMonthEnd);

        double growthToday = calculateGrowth(revenueToday, prevRevenueToday);
        double growthWeek = calculateGrowth(revenueThisWeek, prevRevenueThisWeek);
        double growthMonth = calculateGrowth(revenueThisMonth, prevRevenueThisMonth);

        RevenueSummaryDto revenueSummary = RevenueSummaryDto.builder()
                .today(revenueToday)
                .thisWeek(revenueThisWeek)
                .thisMonth(revenueThisMonth)
                .todayGrowth(growthToday)
                .weekGrowth(growthWeek)
                .monthGrowth(growthMonth)
                .build();

        // Top and Slow products based on period
        List<TopProductDto> topProducts;
        List<TopProductDto> slowSellingProducts;
        if ("all".equalsIgnoreCase(period) || "allTime".equalsIgnoreCase(period)) {
            topProducts = orderItemRepository.findTopSellingProductsAllTime(shopId, PageRequest.of(0, 5)).getContent();
            slowSellingProducts = productRepository.findUnsoldProductsAllTime(shopId, PageRequest.of(0, 5))
                    .getContent().stream()
                    .map(p -> new TopProductDto(p.getId().toString(), p.getName(), 0, BigDecimal.ZERO))
                    .collect(Collectors.toList());
        } else {
            OffsetDateTime periodStart;
            OffsetDateTime periodEnd = now;

            if ("today".equalsIgnoreCase(period)) {
                periodStart = todayStart;
                periodEnd = todayEnd;
            } else if ("thisWeek".equalsIgnoreCase(period)) {
                periodStart = thisWeekStart;
                periodEnd = thisWeekEnd;
            } else {
                periodStart = thisMonthStart;
                periodEnd = thisMonthEnd;
            }
            topProducts = orderItemRepository.findTopSellingProducts(shopId, periodStart, periodEnd, PageRequest.of(0, 5)).getContent();
            slowSellingProducts = productRepository.findUnsoldProducts(shopId, periodStart, periodEnd, PageRequest.of(0, 5))
                    .getContent().stream()
                    .map(p -> new TopProductDto(p.getId().toString(), p.getName(), 0, BigDecimal.ZERO))
                    .collect(Collectors.toList());
        }

        List<Product> lowStockEntities = productRepository.findProductsWithLowStock(shopId, 5);
        List<ProductResponse> lowStockProducts = lowStockEntities.stream().map(this::mapToProductResponse).collect(Collectors.toList());

        Long totalStockLong = inventoryRepository.sumQuantityByShopId(shopId);
        Integer totalStock = totalStockLong != null ? totalStockLong.intValue() : 0;

        InventorySummaryDto inventory = InventorySummaryDto.builder()
                .totalStock(totalStock)
                .lowStockProducts(lowStockProducts)
                .bestSellingProducts(topProducts)
                .slowSellingProducts(slowSellingProducts)
                .build();

        return AdminDashboardResponse.builder()
                .revenue(revenueSummary)
                .topProducts(topProducts)
                .inventory(inventory)
                .build();
    }

    @Override
    public StaffDashboardResponse getStaffDashboard(String username) {
        User user = getUserByUsername(username);
        UUID shopId = user.getShopId();

        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        OffsetDateTime todayStart = now.toLocalDate().atStartOfDay().atOffset(ZoneOffset.UTC);
        OffsetDateTime todayEnd = todayStart.plusDays(1).minusNanos(1);

        Integer totalOrders = orderRepository.countOrdersInPeriod(shopId, todayStart, todayEnd);
        Integer pending = orderRepository.countOrdersByProcessStateInPeriod(shopId, "PENDING", todayStart, todayEnd);
        Integer confirmed = orderRepository.countOrdersByProcessStateInPeriod(shopId, "CONFIRMED", todayStart, todayEnd);
        Integer cancelled = orderRepository.countOrdersByProcessStateInPeriod(shopId, "CANCELLED", todayStart, todayEnd);
        BigDecimal averageOrderValue = orderRepository.calculateAverageOrderValueInPeriod(shopId, todayStart, todayEnd);

        StaffOrderSummaryDto orderSummary = StaffOrderSummaryDto.builder()
                .totalOrdersToday(totalOrders != null ? totalOrders : 0)
                .pending(pending != null ? pending : 0)
                .confirmed(confirmed != null ? confirmed : 0)
                .cancelled(cancelled != null ? cancelled : 0)
                .averageOrderValue(averageOrderValue != null ? averageOrderValue : BigDecimal.ZERO)
                .build();

        List<Product> lowStockEntities = productRepository.findProductsWithLowStock(shopId, 5);
        List<ProductResponse> lowStockProducts = lowStockEntities.stream().map(this::mapToProductResponse).collect(Collectors.toList());

        return StaffDashboardResponse.builder()
                .orderSummary(orderSummary)
                .lowStockProducts(lowStockProducts)
                .build();
    }

    private BigDecimal getRevenueOrDefault(UUID shopId, OffsetDateTime start, OffsetDateTime end) {
        BigDecimal revenue = orderRepository.calculateRevenue(shopId, start, end);
        return revenue != null ? revenue : BigDecimal.ZERO;
    }

    private double calculateGrowth(BigDecimal current, BigDecimal previous) {
        if (previous == null || previous.compareTo(BigDecimal.ZERO) == 0) {
            return current.compareTo(BigDecimal.ZERO) > 0 ? 100.0 : 0.0;
        }
        BigDecimal growth = current.subtract(previous).divide(previous, 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100"));
        return growth.doubleValue();
    }

    private ProductResponse mapToProductResponse(Product p) {
        return ProductResponse.builder()
                .id(p.getId())
                .code(p.getCode())
                .name(p.getName())
                .price(p.getPrice())
                .shopId(p.getShopId())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .createdBy(p.getCreatedBy())
                .updatedBy(p.getUpdatedBy())
                .categoryId(p.getCategoryId())
                .description(p.getDescription())
                .imgUrl(p.getImgUrl())
                .build();
    }
}
