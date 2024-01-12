package net.invictusmanagement.invictuslifestyle.interfaces;

import net.invictusmanagement.invictuslifestyle.fragments.BrivoDevicesFragment;
import net.invictusmanagement.invictuslifestyle.models.BrivoDeviceData;

public interface BrivoDevicesListFragmentInteractionListener {
    void onListFragmentInteraction(BrivoDeviceData item, BrivoDevicesFragment brivoDevicesFragment,int position);
}
