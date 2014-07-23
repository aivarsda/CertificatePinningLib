package com.aivarsda.certpinninglib;

import com.aivarsda.certpinninglib.network.PinnedConnectionResponse;


public interface IPinnerCallback
{
	void onTaskPinningSuccess(PinnedConnectionResponse pinnedConnectionResponse);
	void onTaskPinningFailure(PinnedConnectionResponse pinnedConnectionResponse);
}
