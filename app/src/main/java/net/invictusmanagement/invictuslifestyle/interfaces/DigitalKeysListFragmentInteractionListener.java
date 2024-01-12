package net.invictusmanagement.invictuslifestyle.interfaces;

import net.invictusmanagement.invictuslifestyle.fragments.AllDigitalKeysFragment;
import net.invictusmanagement.invictuslifestyle.models.DigitalKey;

public interface DigitalKeysListFragmentInteractionListener {
    void onListFragmentInteraction(DigitalKey item, AllDigitalKeysFragment digitalKeysFragment);

    void onRenewClicked(DigitalKey item, AllDigitalKeysFragment digitalKeysFragment);

    void onRevokeClicked(DigitalKey item, AllDigitalKeysFragment digitalKeysFragment);
}
