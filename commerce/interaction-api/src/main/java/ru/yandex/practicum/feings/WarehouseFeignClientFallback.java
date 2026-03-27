package ru.yandex.practicum.feings;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.dto.*;

import java.util.Map;
import java.util.UUID;


@Component
public class WarehouseFeignClientFallback implements WarehouseProductFeignClient {
    @Override
    public void registerNewProduct(NewProductInWarehouseRequest request) {
        throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "склад не доступен, ждем отстоя пены ;-)");
    }

    @Override
    public BookedProductsDto reserveProducts(ShoppingCartDto cart) {
        throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "склад не доступен, ждем отстоя пены ;-)");
    }

    @Override
    public void addQuantity(AddProductToWarehouseRequest request) {
        throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "склад не доступен, ждем отстоя пены ;-)");
    }

    @Override
    public AddressDto getAddressWarehouse() {
        throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "склад не доступен, ждем отстоя пены ;-)");
    }

    @Override
    public void shippedToDelivery(ShipperToDeliveryRequest request) {
        throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Warehouse temporarily unavailable. Please try again later.");
    }

    @Override
    public void returnProducts(Map<UUID, Long> products) {
        throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Warehouse temporarily unavailable. Please try again later.");
    }

    @Override
    public BookedProductsDto assemblyProductsForOrder(AssemblyProductsForOrderRequest request) {
        throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Warehouse temporarily unavailable. Please try again later.");
    }
}