
package com.aidn5.mcqa;

/**
 * Class contains all the permissions nodes.
 * 
 * @author aidn5
 */
public class McqaPermission {
  private McqaPermission() {
    throw new AssertionError();
  }

  /**
   * Whether the person is allowed to view contents.
   */
  public static final String VIEW_CONTENTS = "mcqa.viewContents";
  /**
   * Whether the person is allowed to view <b>ALL contents (approved and not
   * approved)</b>.
   */
  public static final String VIEW_ALL_CONTETNS = "mcqa.getAllContents";
  /**
   * Whether the person is allowed approve contents to be seen by the public.
   */
  public static final String APPROVE_CONTENTS = "mcqa.approveContents";
  /**
   * Whether the person can edit public contents.
   */
  public static final String EDIT_CONTENTS = "mcqa.editContents";
  /**
   * Whether the person can remove public contents.
   */
  public static final String REMOVE_CONTENTS = "mcqa.removeContents";
  /**
   * Whether the person can add new content to the database.
   */
  public static final String ADD_CONTENTS = "mcqa.addContents";
  /**
   * Whether the person can copy contents to a book (or book and quill).
   * Useful to save a copy/edit contents and re-upload them.
   */
  public static final String COPY_CONTENTS = "mcqa.copyContents";
  /**
   * Whether the person can copy contents to a book (or book and quill) and
   * <i>bypass the requirement of sacrificing a book from their inventory.</i>
   */
  public static final String BypassCopyContentsRequireBook = "mcqa.copyContents.bypassRequireBook";

  /**
   * Allow access to /qaadmin command, its info and its help.
   */
  public static final String ADMIN = "mcqa.admin";
  /**
   * Allow to get a book of the plugin's tutorial through the admin command.
   */
  public static final String ADMIN_BOOK = "mcqa.admin.book";
  /**
   * Allow to migrate data from database to another. Useful to migrate data or/and
   * to fill database based on memory.
   */
  public static final String ADMIN_MIGRATE = "mcqa.admin.migrate";
  /**
   * Allow to disable/enable the internal database and to reload the plugin.
   */
  public static final String ADMIN_RELOAD = "mcqa.admin.reload";
}
