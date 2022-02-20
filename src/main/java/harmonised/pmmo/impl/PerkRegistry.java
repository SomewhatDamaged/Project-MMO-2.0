package harmonised.pmmo.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.function.TriFunction;

import com.google.common.base.Preconditions;
import com.google.common.collect.LinkedListMultimap;
import harmonised.pmmo.api.enums.EventType;
import harmonised.pmmo.core.Core;
import harmonised.pmmo.util.MsLoggy;
import harmonised.pmmo.util.TagUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.LogicalSide;

public class PerkRegistry {
	public PerkRegistry() {}
	
	private Map<ResourceLocation, TriFunction<Player, CompoundTag, Integer, CompoundTag>> perkExecutions = new HashMap<>();
	private Map<ResourceLocation, TriFunction<Player, CompoundTag, Integer, CompoundTag>> perkTerminations = new HashMap<>();
	private Map<EventType, LinkedListMultimap<String, CompoundTag>> perkSettings = new HashMap<>();
	
	public void setSettings(Map<EventType, LinkedListMultimap<String, CompoundTag>> settings) {
		perkSettings = settings;
	}
	public Map<EventType, LinkedListMultimap<String, CompoundTag>> getSettings() {return perkSettings;}
	
	public void registerPerk(
			ResourceLocation perkID, 
			TriFunction<Player, CompoundTag, Integer, CompoundTag> onExecute, 
			TriFunction<Player, CompoundTag, Integer, CompoundTag> onConclude) {
		Preconditions.checkNotNull(perkID);
		Preconditions.checkNotNull(onExecute);
		Preconditions.checkNotNull(onConclude);
		perkExecutions.put(perkID, onExecute);
		perkTerminations.put(perkID, onConclude);
		MsLoggy.debug("Registered Perk: "+perkID.toString());
	}
	
	public CompoundTag executePerk(EventType cause, Player player, LogicalSide side) {
		return executePerk(cause, player, new CompoundTag(), side);
	}
	
	public CompoundTag executePerk(EventType cause, Player player, CompoundTag dataIn, LogicalSide side) {
		LinkedListMultimap<String, CompoundTag> map =  perkSettings.getOrDefault(cause, LinkedListMultimap.create());
		CompoundTag output = new CompoundTag();
		for (String skill : map.keySet()) {
			List<CompoundTag> entries = map.get(skill);
			int skillLevel = Core.get(side).getData().getPlayerSkillLevel(skill, player.getUUID());
			for (int i = 0; i < entries.size(); i++) {
				CompoundTag src = entries.get(i);
				src.merge(dataIn);
				ResourceLocation perkID = new ResourceLocation(src.getString("perk"));
				CompoundTag executionOutput = new CompoundTag();
				executionOutput = perkExecutions.getOrDefault(perkID, (plyr, nbt, level) -> new CompoundTag()).apply(player, src, skillLevel);
				output = TagUtils.mergeTags(output, executionOutput);
			}
		}
		return output;
	}
	
	public CompoundTag terminatePerk(EventType cause, Player player, LogicalSide side) {
		return terminatePerk(cause, player, new CompoundTag(), side);
	}
	
	public CompoundTag terminatePerk(EventType cause, Player player, CompoundTag dataIn, LogicalSide side) {
		LinkedListMultimap<String, CompoundTag> map = perkSettings.getOrDefault(cause, LinkedListMultimap.create());
		CompoundTag output = new CompoundTag();
		for (String skill : map.keySet()) {
			List<CompoundTag> entries = map.get(skill);
			int skillLevel = Core.get(side).getData().getPlayerSkillLevel(skill, player.getUUID());
			for (int i = 0; i < entries.size(); i++) {
				CompoundTag src = entries.get(i);
				src.merge(dataIn);
				ResourceLocation perkID = new ResourceLocation(src.getString("perk"));
				CompoundTag executionOutput = new CompoundTag();
				executionOutput = perkTerminations.getOrDefault(perkID, (plyr, nbt, level) -> new CompoundTag()).apply(player, src, skillLevel);
				output = TagUtils.mergeTags(output, executionOutput);
			}
		}
		return output;
	}
}
