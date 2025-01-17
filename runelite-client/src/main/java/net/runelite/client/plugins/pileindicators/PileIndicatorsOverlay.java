/*
 * Copyright (c) 2019, gazivodag <https://github.com/gazivodag>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package net.runelite.client.plugins.pileindicators;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.Actor;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.OverlayUtil;

@Singleton
public class PileIndicatorsOverlay extends Overlay
{
	private final PileIndicatorsPlugin plugin;
	private final PileIndicatorsConfig config;

	@Inject
	PileIndicatorsOverlay(final PileIndicatorsPlugin plugin, final PileIndicatorsConfig config)
	{
		super(plugin);
		this.plugin = plugin;
		this.config = config;
		setLayer(OverlayLayer.AFTER_MIRROR);
		setPosition(OverlayPosition.DYNAMIC);
		setPriority(OverlayPriority.HIGH);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		List<List<Actor>> stackList = plugin.getStacks();

		if (stackList != null)
		{
			for (List<Actor> actorArrayList : stackList)
			{
				PileType pileType = plugin.getPileType(actorArrayList);
				Color pileColor = plugin.getColorByPileType(pileType);

				try
				{
					Actor actorToRender = actorArrayList.get(0); //guaranteed to have at least two players
					final String pileTypeStr = pileType == PileType.PLAYER_PILE ? "PLAYER" : pileType == PileType.NPC_PILE ? "NPC" : pileType == PileType.MIXED_PILE ? "MIXED" : "";
					final String text = config.numberOnly() ? "" + actorArrayList.size() : (pileTypeStr + " PILE SIZE: " + actorArrayList.size());
					if (config.drawPileTile())
					{
						OverlayUtil.renderPolygon(graphics, actorToRender.getCanvasTilePoly(), pileColor);
					}
					if (config.drawPileHull())
					{
						OverlayUtil.renderPolygon(graphics, actorToRender.getConvexHull(), pileColor);
					}
					OverlayUtil.renderTextLocation(graphics, actorToRender.getCanvasTextLocation(graphics, text, 40), text, pileColor);
				}
				catch (Exception ignored)
				{
				}
			}
		}

		return null;
	}
}