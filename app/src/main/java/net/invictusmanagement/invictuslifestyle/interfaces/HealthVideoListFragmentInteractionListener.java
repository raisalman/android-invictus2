package net.invictusmanagement.invictuslifestyle.interfaces;

import net.invictusmanagement.invictuslifestyle.models.HealthVideo;

public interface HealthVideoListFragmentInteractionListener {
    void watchVideo(HealthVideo item, long id, String createdUtc);
}
