package com.github.commoble.barterbox.client;

import com.github.commoble.barterbox.barter_box.BarterBoxSellerContainer;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class BarterBoxSellerScreen extends ContainerScreen<BarterBoxSellerContainer>
{
	public static final ResourceLocation GUI_TEXTURE = new ResourceLocation("barterbox:textures/gui/barter_box_seller.png");
	
	public BarterBoxSellerScreen(BarterBoxSellerContainer screenContainer, PlayerInventory inv, ITextComponent titleIn)
	{
		super(screenContainer, inv, titleIn);
		// size of the texture background we want to blit
		this.xSize = 176;
		this.ySize = 222;
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks)
	{
		this.renderBackground();
		super.render(mouseX, mouseY, partialTicks);
		this.renderHoveredToolTip(mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
	{
		int xStart = (this.width - this.xSize) / 2;
		int yStart = (this.height - this.ySize) / 2;
		
		// draw the background
		this.minecraft.getTextureManager().bindTexture(GUI_TEXTURE);
		this.blit(xStart, yStart, 0,0, this.xSize, this.ySize);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		String playerName = this.playerInventory.getName().getFormattedText();
		// positions and colors from ChestScreen
		this.font.drawString(this.title.getFormattedText(), 8.0F, 6.0F, 4210752);
		this.font.drawString(this.playerInventory.getDisplayName().getFormattedText(), 8.0F, this.ySize - 96 + 2, 4210752);
	}

}
