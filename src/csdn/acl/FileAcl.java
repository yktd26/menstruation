/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package csdn.acl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.AclEntry;
import java.nio.file.attribute.AclEntryPermission;
import java.nio.file.attribute.AclEntryType;
import java.nio.file.attribute.AclFileAttributeView;
import java.nio.file.attribute.UserPrincipal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author vdmdev2
 */
public class FileAcl {
    
    @SuppressWarnings("empty-statement")
    public static void main(String... args) throws IOException{
        //Build file path
        Path file = new File("FilePath").toPath();
        //Read Acl
        AclFileAttributeView view = Files.getFileAttributeView(file, AclFileAttributeView.class);
        List<AclEntry> acl = view.getAcl();
        for(AclEntry ace: acl){
            System.out.printf("Ace Type: %s , Principal: %s\r\n", ace.type().name(), ace.principal().getName());
            String permsStr = "";
            for(AclEntryPermission perm : ace.permissions()){
                permsStr += perm.name()+" ";
            }
            System.out.printf("Ace Permissions: %s\r\n\r\n", permsStr.trim());
        }
        
        //Add Acl
        //Get user
        UserPrincipal user = file.getFileSystem().getUserPrincipalLookupService().lookupPrincipalByName("Username");
        AclEntry entry = AclEntry.newBuilder()
         .setType(AclEntryType.ALLOW)
         .setPrincipal(user)
         .setPermissions(AclEntryPermission.READ_DATA, AclEntryPermission.READ_ATTRIBUTES )
         .build();
        acl.add(0, entry);   // insert before any DENY entries
        view.setAcl(acl);
        
    }
}
