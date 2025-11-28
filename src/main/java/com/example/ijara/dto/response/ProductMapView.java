package com.example.ijara.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductMapView {

    private UUID id;
    private double lat;
    private double lng;
    private String coverImage;
    private String name;
    private double rating;
}
