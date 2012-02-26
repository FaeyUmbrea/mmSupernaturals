package me.matterz.supernaturals.events;

public enum ConvertReason {

	/**
	 * The player was converted by another player using a spell.
	 */
	PLAYER_CONVERT_SPELL(),
	/**
	 * The player died in a certain way triggering conversion.
	 */
	PLAYER_DIED(),
	/**
	 * The player interacted with a block triggering conversion.
	 */
	PLAYER_INTERACTED_BLOCK(),
	/**
	 * An entity killed the player triggering conversion.
	 */
	ENTITY_KILLED_PLAYER(),
	/**
	 * A player killed the converted player triggering conversion, or the converted player killed a player triggering conversion.
	 */
	PLAYER_KILLED_PLAYER(),
	/**
	 * The player was converted with the convert command.
	 */
	COMMAND();

}
