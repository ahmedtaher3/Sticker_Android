package com.sticker_android.model.interfaces;

import com.sticker_android.model.contest.OngoingContest;
import com.sticker_android.model.corporateproduct.Product;

/**
 * Created by user on 19/4/18.
 */

public interface ContentApprovalActionListener {
    void onRemove(OngoingContest ongoingContest);

    void onResubmit(OngoingContest ongoingContest);
}
