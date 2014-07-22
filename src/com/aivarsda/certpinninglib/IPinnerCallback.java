package com.aivarsda.certpinninglib;

import com.aivarsda.certpinninglib.network.PinnedConnectionResponse;


public interface IPinnerCallback
{
	void onTaskPinningCompleted(PinnedConnectionResponse pinnedConnectionResponse);
}
