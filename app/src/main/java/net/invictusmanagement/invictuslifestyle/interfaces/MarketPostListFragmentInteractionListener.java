package net.invictusmanagement.invictuslifestyle.interfaces;

import net.invictusmanagement.invictuslifestyle.models.BulletinBoard;

public interface MarketPostListFragmentInteractionListener {
    void onListFragmentInteraction(BulletinBoard item);

    void onListFragmentForEditInteraction(BulletinBoard item);
}
