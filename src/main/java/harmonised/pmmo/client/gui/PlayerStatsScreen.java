package harmonised.pmmo.client.gui;

import harmonised.pmmo.client.gui.component.PMMOButton;
import harmonised.pmmo.client.gui.component.PlayerStatsComponent;
import harmonised.pmmo.config.Config;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;

public class PlayerStatsScreen extends EffectRenderingInventoryScreen<InventoryMenu> {
    private final PlayerStatsComponent playerStatsComponent = new PlayerStatsComponent();
    
    public float xMouse;
    public float yMouse;
    public boolean widthTooNarrow;
    
    public PlayerStatsScreen(Player player) {
        super(player.inventoryMenu, player.getInventory(), Component.translatable("container.crafting"));
        this.titleLabelX = 97;
    }
    
    @Override
    protected void init() {
        super.init();
        this.widthTooNarrow = this.width < 379;
        
        this.playerStatsComponent.init(this.width, this.height, this.minecraft, this.widthTooNarrow);
        this.playerStatsComponent.toggleVisibility();
        this.leftPos = this.playerStatsComponent.updateScreenPosition(this.width, this.imageWidth);
        
        this.addRenderableWidget(new PMMOButton(this, this.leftPos + Config.SKILL_BUTTON_X.get() - 22, this.height / 2 +Config.SKILL_BUTTON_Y.get(), 20, 18));
        this.addWidget(this.playerStatsComponent);
    }
    
    protected void containerTick() {
        this.playerStatsComponent.tick();
    }
    
    @Override
    public void render(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderBackground(graphics, pMouseX, pMouseY, pPartialTick);
        if (this.playerStatsComponent.isVisible() && this.widthTooNarrow) {
            this.renderBg(graphics, pPartialTick, pMouseX, pMouseY);
            this.playerStatsComponent.render(graphics, pMouseX, pMouseY, pPartialTick);
        } else {
            this.playerStatsComponent.render(graphics, pMouseX, pMouseY, pPartialTick);
            super.render(graphics, pMouseX, pMouseY, pPartialTick);
        }
    
        this.renderTooltip(graphics, pMouseX, pMouseY);
        this.xMouse = (float)pMouseX;
        this.yMouse = (float)pMouseY;
    }
    
    @Override
    protected void renderLabels(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {
        pGuiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752, false);
    }
    
    @Override
    protected void renderBg(GuiGraphics graphics, float partial, int mouseX, int mouseY) {
        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        int i = this.leftPos;
        int j = this.topPos;
        graphics.blit(INVENTORY_LOCATION, i, j, 0, 0, this.imageWidth, this.imageHeight);
        InventoryScreen.renderEntityInInventoryFollowsMouse(graphics, i + 26, j + 8, i + 75, j + 78, 30, 0.06F, (float) mouseX, (float) mouseY, this.minecraft.player);
    }
    
    protected boolean isHovering(int pX, int pY, int pWidth, int pHeight, double pMouseX, double pMouseY) {
        return (!this.widthTooNarrow || !this.playerStatsComponent.isVisible()) && super.isHovering(pX, pY, pWidth, pHeight, pMouseX, pMouseY);
    }
    
    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if (this.playerStatsComponent.mouseClicked(pMouseX, pMouseY, pButton)) {
            this.setFocused(this.playerStatsComponent);
            return true;
        }
        return (!this.widthTooNarrow || !this.playerStatsComponent.isVisible()) && super.mouseClicked(pMouseX, pMouseY, pButton);
    }
    
    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        if (this.playerStatsComponent.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY)) {
            return true;
        }
        return super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
    }
    
    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta, double other) {
        if (this.playerStatsComponent.mouseScrolled(pMouseX, pMouseY, pDelta, other)) {
            return true;
        }
        return super.mouseScrolled(pMouseX, pMouseY, pDelta, other);
    }
    
    public PlayerStatsComponent getPlayerStatsComponent() {
        return playerStatsComponent;
    }
}
