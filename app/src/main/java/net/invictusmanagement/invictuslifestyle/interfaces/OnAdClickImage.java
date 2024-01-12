package net.invictusmanagement.invictuslifestyle.interfaces;

import net.invictusmanagement.invictuslifestyle.models.CouponsAdvertisement;

import java.util.List;

public interface OnAdClickImage {
    void onAdClickImage(List<CouponsAdvertisement> couponsAdvertisements, int position);
}
