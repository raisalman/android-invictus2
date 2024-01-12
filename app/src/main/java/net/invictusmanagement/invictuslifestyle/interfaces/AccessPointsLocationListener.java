package net.invictusmanagement.invictuslifestyle.interfaces;

import net.invictusmanagement.invictuslifestyle.fragments.AccessPointsFragment;
import net.invictusmanagement.invictuslifestyle.fragments.GuestAccessPointsFragment;
import net.invictusmanagement.invictuslifestyle.models.AccessPoint;

public interface AccessPointsLocationListener {
    void onLocationSelected(AccessPoint item, int position);
}
