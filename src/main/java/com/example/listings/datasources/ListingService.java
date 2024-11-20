package com.example.listings.datasources;

import com.example.listings.generated.types.Amenity;
import com.example.listings.generated.types.CreateListingInput;
import com.example.listings.models.CreateListingModel;
import com.example.listings.models.ListingModel;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.util.List;

@Component
public class ListingService
{
    private static final String LISTING_API_URL = "https://rt-airlock-services-listing.herokuapp.com";
    private final RestClient client = RestClient.builder().baseUrl(LISTING_API_URL).build();
    private final ObjectMapper mapper = new ObjectMapper();

    public List<ListingModel> featuredListingsRequest() throws IOException {
        JsonNode response =  client
                .get()
                .uri("/featured-listings")
                .retrieve()
                .body(JsonNode.class);
        if (response != null)
        {
            return mapper.readValue(response.traverse(), new TypeReference<>() {});
        }
        return null;
    }

    public ListingModel listingRequest(String id)
    {
        return client
                .get()
                .uri("/listings/{listing_id}", id)
                .retrieve()
                .body(ListingModel.class);
    }

    public List<Amenity> amenitiesRequest(String listingId) throws IOException {
        JsonNode response = client
                .get()
                .uri("/listings/{listing_id}/amenities", listingId)
                .retrieve()
                .body(JsonNode.class);

        if (response != null) {
            return mapper.readValue(response.traverse(), new TypeReference<List<Amenity>>() {});
        }

        return null;
    }

    public ListingModel createListingRequest(CreateListingInput listing)
    {
        MappingJacksonValue serializedListing = new MappingJacksonValue(new CreateListingModel(listing));
        return client.post()
                .uri("/listings")
                .body(serializedListing)
                .retrieve()
                .body(ListingModel.class);
    }
}
