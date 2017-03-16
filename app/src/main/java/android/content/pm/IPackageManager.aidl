/*
**
** Copyright 2007, The Android Open Source Project
**
** Licensed under the Apache License, Version 2.0 (the "License");
** you may not use this file except in compliance with the License.
** You may obtain a copy of the License at
**
**     http://www.apache.org/licenses/LICENSE-2.0
**
** Unless required by applicable law or agreed to in writing, software
** distributed under the License is distributed on an "AS IS" BASIS,
** WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
** See the License for the specific language governing permissions and
** limitations under the License.
*/

package android.content.pm;

import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.FeatureInfo;
import android.content.pm.IPackageInstallObserver;
import android.content.pm.IPackageDeleteObserver;
import android.content.pm.IPackageMoveObserver;
import android.content.pm.InstrumentationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.ProviderInfo;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.net.Uri;
import android.content.IntentSender;

/**
 *  See {@link PackageManager} for documentation on most of the APIs
 *  here.
 * 
 *  {@hide}
 */
interface IPackageManager {


    /**
     * Install a package.
     *
     * @param packageURI The location of the package file to install.
     * @param observer a callback to use to notify when the package installation in finished.
     * @param flags - possible values: {@link #FORWARD_LOCK_PACKAGE},
     * {@link #REPLACE_EXISITING_PACKAGE}
     * @param installerPackageName Optional package name of the application that is performing the
     * installation. This identifies which market the package came from.
     */
    void installPackage(in Uri packageURI, IPackageInstallObserver observer, int flags,
            in String installerPackageName);

    void finishPackageInstall(int token);

    /**
     * Delete a package.
     *
     * @param packageName The fully qualified name of the package to delete.
     * @param observer a callback to use to notify when the package deletion in finished.
     * @param flags - possible values: {@link #DONT_DELETE_DATA}
     */
    void deletePackage(in String packageName, IPackageDeleteObserver observer, int flags);


}
