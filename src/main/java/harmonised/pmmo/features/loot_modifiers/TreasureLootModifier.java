package harmonised.pmmo.features.loot_modifiers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import harmonised.pmmo.config.Config;
import harmonised.pmmo.core.Core;
import harmonised.pmmo.setup.datagen.LangProvider;
import harmonised.pmmo.util.Reference;
import harmonised.pmmo.util.RegistryUtil;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class TreasureLootModifier extends LootModifier{

	
	public static final MapCodec<TreasureLootModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> codecStart(instance).and(instance.group(
			ItemStack.CODEC.optionalFieldOf("item").forGetter(tlm -> tlm.drop),
			Codec.INT.fieldOf("count").forGetter(tlm -> tlm.count),
			Codec.DOUBLE.fieldOf("chance").forGetter(tlm -> tlm.chance),
			Codec.BOOL.optionalFieldOf("per_level").forGetter(tlm -> Optional.of(tlm.perLevel)),
			Codec.STRING.optionalFieldOf("skill").forGetter(tlm -> Optional.of(tlm.skill))
			)).apply(instance, TreasureLootModifier::new));

	public Optional<ItemStack> drop;
	public final int count;
	public double chance;
	public boolean perLevel;
	public String skill;

	public TreasureLootModifier(LootItemCondition[] conditionsIn, Optional<ItemStack> lootItem, int count, double chance) {
		this(conditionsIn, lootItem, count, chance, Optional.of(false), Optional.empty());
	}
	public TreasureLootModifier(LootItemCondition[] conditionsIn, Optional<ItemStack> lootItem, int count,
								double chance, Optional<Boolean> perLevel, Optional<String> skill) {
		super(conditionsIn);
		this.chance = chance;
		this.drop = lootItem;
		if (drop.isPresent())
			this.drop.get().setCount(count);
		this.count = count;
		this.perLevel = perLevel.orElse(false);
		this.skill = skill.orElse("");
	}

	public LootItemCondition[] getConditions() {return this.conditions;}

	@Override
	public MapCodec<? extends IGlobalLootModifier> codec() {
		return CODEC;
	}

	@Override
	protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot,	LootContext context) {
		if (!Config.server().general().treasureEnabled()) return generatedLoot;
		if (perLevel && context.getParam(LootContextParams.THIS_ENTITY) instanceof Player player) {
			chance *= Core.get(player.level()).getData().getLevel(skill, player.getUUID());
		}
		if (context.getRandom().nextDouble() <= chance) {
			
			//this section checks if the drop is air and replaces it with the block
			//being broken.  this is the logic for Extra Drops
			BlockState state = context.getParamOrNull(LootContextParams.BLOCK_STATE);
			if (state != null && drop.isEmpty()) {
				drop = Optional.of(state.getDrops(builderFromContext(context)).get(0));
				drop.get().setCount(count);
			}
			
			//Notify player that their skill awarded them an extra drop.
			Entity breaker = context.getParamOrNull(LootContextParams.THIS_ENTITY);
			if (breaker instanceof Player player) {
				player.sendSystemMessage(LangProvider.FOUND_TREASURE.asComponent());
			}
			generatedLoot.add(drop.get().copy());
		}
		return generatedLoot;
	}

	private LootParams.Builder builderFromContext(LootContext context) {
		return new LootParams.Builder(context.getLevel())
				.withParameter(LootContextParams.ORIGIN, context.getParam(LootContextParams.ORIGIN))
				.withParameter(LootContextParams.TOOL, context.getParam(LootContextParams.TOOL));
	}
}
