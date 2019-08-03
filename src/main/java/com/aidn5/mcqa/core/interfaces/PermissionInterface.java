package com.aidn5.mcqa.core.interfaces;

import java.util.UUID;

public interface PermissionInterface {
	/**
	 * Is this person allowed to view and search contents?
	 * 
	 * @param uuid
	 *            the unique id of the person
	 * 
	 * @return TRUE if person is allowed. FALSE if not
	 */
	public boolean canViewContents(UUID uuid);

	/**
	 * Is this person allowed to <b>ALL content (approved and not approved)</b> view
	 * and search contents?
	 * 
	 * @param uuid
	 *            the unique id of the person
	 * 
	 * @return TRUE if person is allowed. FALSE if not
	 */
	public boolean canViewAllContents(UUID uuid);

	/**
	 * Is this person allowed to approve contents to be seen by the public
	 * 
	 * @param uuid
	 *            the unique id of the person
	 * 
	 * @return TRUE if person is allowed. FALSE if not
	 */
	public boolean canApproveContents(UUID uuid);

	/**
	 * Is this person allowed to approve contents to be seen by the public
	 * 
	 * @param uuid
	 *            the unique id of the person
	 * 
	 * @return TRUE if person is allowed. FALSE if not
	 */
	public boolean canEditContents(UUID uuid);

	/**
	 * Is this person allowed to remove contents
	 * 
	 * @param uuid
	 *            the unique id of the person
	 * 
	 * @return TRUE if person is allowed. FALSE if not
	 */
	public boolean canRemoveContents(UUID uuid);

	/**
	 * can this person add new contents to the database?
	 * 
	 * @param uuid
	 *            the unique id of the person
	 * 
	 * @return TRUE if person is allowed. FALSE if not
	 */
	public boolean canAddContents(UUID uuid);

	/**
	 * can this person copy contents to a book (or book and quill)
	 * 
	 * @param uuid
	 *            the unique id of the person
	 * 
	 * @return TRUE if person is allowed. FALSE if not
	 */
	public boolean canCopyContents(UUID uuid);

	/**
	 * can this person copy contents to a book (or book and quill) and <i>bypass the
	 * requirement of sacrificing a book from their inventory
	 * 
	 * @param uuid
	 *            the unique id of the person
	 * 
	 * @return TRUE if person is allowed. FALSE if not
	 */
	public boolean canBypassCopyContentsRequireBook(UUID uuid);

	/**
	 * can this person change/edit/delete/hide/approve contents?
	 * 
	 * @param uuid
	 *            the unique id of the person
	 * 
	 * @return TRUE if person is allowed. FALSE if not
	 */
	public boolean isPersonAuthority(UUID uuid);
}
