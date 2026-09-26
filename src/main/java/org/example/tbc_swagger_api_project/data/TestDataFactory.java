package org.example.tbc_swagger_api_project.data;

import org.example.tbc_swagger_api_project.models.Category;
import org.example.tbc_swagger_api_project.models.Order;
import org.example.tbc_swagger_api_project.models.Pet;
import org.example.tbc_swagger_api_project.models.Tag;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public final class TestDataFactory {

    // The public Petstore is shared by everyone, so use a high random range to avoid colliding with other users' data.
    private static final long MIN_ID = 9_000_000_000L;
    private static final long MAX_ID = 9_999_999_999L;

    private TestDataFactory() {
    }

    public static long uniqueId() {
        return ThreadLocalRandom.current().nextLong(MIN_ID, MAX_ID);
    }

    public static Pet pet(String name, String categoryName, String status) {
        return Pet.builder()
                .id(uniqueId())
                .name(name)
                .category(Category.builder().id(1L).name(categoryName).build())
                .photoUrls(List.of("https://example.com/photos/" + name.toLowerCase().replace(' ', '-') + ".jpg"))
                .tags(List.of(
                        Tag.builder().id(1L).name("automation").build(),
                        Tag.builder().id(2L).name(categoryName.toLowerCase()).build()))
                .status(status)
                .build();
    }

    public static Order order(long petId, int quantity, String status) {
        return Order.builder()
                .id(uniqueId())
                .petId(petId)
                .quantity(quantity)
                .status(status)
                .complete(false)
                .build();
    }
}
