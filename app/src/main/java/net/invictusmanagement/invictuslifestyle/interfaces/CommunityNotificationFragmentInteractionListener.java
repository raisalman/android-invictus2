package net.invictusmanagement.invictuslifestyle.interfaces;

import net.invictusmanagement.invictuslifestyle.fragments.CommunityNotificationFragment;
import net.invictusmanagement.invictuslifestyle.models.CommunityNotificationList;

public interface CommunityNotificationFragmentInteractionListener {
    void onListFragmentInteraction(CommunityNotificationList item, CommunityNotificationFragment fragment);
    void refreshList();
//    void onRevokeClicked(ServiceKey item, ServiceKeysFragment serviceKeysFragment);
}
