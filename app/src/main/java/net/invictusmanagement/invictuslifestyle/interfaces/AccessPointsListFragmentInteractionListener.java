package net.invictusmanagement.invictuslifestyle.interfaces;

import net.invictusmanagement.invictuslifestyle.fragments.AccessPointsFragment;
import net.invictusmanagement.invictuslifestyle.fragments.GuestAccessPointsFragment;
import net.invictusmanagement.invictuslifestyle.models.AccessPoint;

public interface AccessPointsListFragmentInteractionListener {
    void onListFragmentInteraction(AccessPoint item, int position, AccessPointsFragment accessPointsFragment);

    void onSlideUnlockTapped(int position, AccessPoint item, AccessPointsFragment accessPointsFragment);

    void onGuestSlideUnlockTapped(int position, AccessPoint item, GuestAccessPointsFragment accessPointsFragment);
}
