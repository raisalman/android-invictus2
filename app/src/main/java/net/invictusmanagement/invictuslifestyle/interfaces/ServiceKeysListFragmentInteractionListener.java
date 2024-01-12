package net.invictusmanagement.invictuslifestyle.interfaces;

import net.invictusmanagement.invictuslifestyle.fragments.ServiceKeysFragment;
import net.invictusmanagement.invictuslifestyle.models.ServiceKey;

public interface ServiceKeysListFragmentInteractionListener {
    void onListFragmentInteraction(ServiceKey item, ServiceKeysFragment serviceKeysFragment);

    void onRevokeClicked(ServiceKey item, ServiceKeysFragment serviceKeysFragment);
}
