package com.aidn5.mcqa.defaults;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import com.aidn5.mcqa.core.interfaces.PermissionInterface;

/**
 * Default permission adapter, uses default permissions nodes
 * 
 * @author aidn5
 *
 */
public class DefaultPermissionAdapter implements PermissionInterface {

	@Override
	public boolean canViewContents(UUID uuid) {
		return check("mcqa.viewContents", uuid);
	}

	@Override
	public boolean canViewAllContents(UUID uuid) {
		return check("mcqa.getAllContents", uuid);
	}

	@Override
	public boolean canApproveContents(UUID uuid) {
		return check("mcqa.approveContents", uuid);
	}

	@Override
	public boolean canEditContents(UUID uuid) {
		return check("mcqa.editContents", uuid);
	}

	@Override
	public boolean canRemoveContents(UUID uuid) {
		return check("mcqa.removeContents", uuid);
	}

	@Override
	public boolean canAddContents(UUID uuid) {
		return check("mcqa.addContents", uuid);
	}

	@Override
	public boolean canCopyContents(UUID uuid) {
		return check("mcqa.copyContents", uuid);
	}

	@Override
	public boolean canBypassCopyContentsRequireBook(UUID uuid) {
		return check("mcqa.copyContents.bypassRequireBook", uuid);
	}

	@Override
	public boolean isPersonAuthority(UUID uuid) {
		return check("mcqa.*", uuid);
	}

	private static boolean check(String permission, UUID uuid) {
		Player player = Bukkit.getPlayer(uuid);
		if (player == null) return false; // ghost?!

		Permission p = Bukkit.getPluginManager().getPermission(permission);
		if (player.isPermissionSet(p)) return player.hasPermission(p);
		return p.getDefault() == PermissionDefault.TRUE || player.isOp();
	}
}
