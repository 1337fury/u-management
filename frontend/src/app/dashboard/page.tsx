"use client";

import { useState, useEffect } from "react";
import { useAuth } from "@/context/AuthContext";
import { userAPI } from "@/services/api";
import { useRouter } from "next/navigation";
import toast from "react-hot-toast";
import UserSearch from "./user-search";

interface UploadResult {
  totalRecords: number;
  successCount: number;
  failedCount: number;
}

export default function DashboardPage() {
  const { user, isLoading, isAuthenticated, logout } = useAuth();
  const router = useRouter();
  const [generatingUsers, setGeneratingUsers] = useState(false);
  const [userCount, setUserCount] = useState(10);
  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  const [uploadResult, setUploadResult] = useState<UploadResult | null>(null);
  const [isUploading, setIsUploading] = useState(false);

  useEffect(() => {
    if (!isLoading && !isAuthenticated) {
      router.push("/login");
    }
  }, [isLoading, isAuthenticated, router]);

  const handleGenerateUsers = async () => {
    setGeneratingUsers(true);
    try {
      const blob = await userAPI.generateUsers(userCount);

      const url = window.URL.createObjectURL(blob);
      const a = document.createElement("a");
      a.href = url;
      a.download = `generated-users-${userCount}.json`;
      document.body.appendChild(a);
      a.click();

      window.URL.revokeObjectURL(url);
      document.body.removeChild(a);

      toast.success(`Successfully generated ${userCount} users!`);
    } catch (error) {
      console.error("Error generating users:", error);
      toast.error("Failed to generate users");
    } finally {
      setGeneratingUsers(false);
    }
  };

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files[0]) {
      setSelectedFile(e.target.files[0]);
    }
  };

  const handleFileUpload = async () => {
    if (!selectedFile) {
      toast.error("Please select a file first");
      return;
    }

    setIsUploading(true);
    try {
      const result = await userAPI.batchImportUsers(selectedFile);
      setUploadResult(result);
      toast.success("File uploaded successfully!");
    } catch (error) {
      console.error("Error uploading file:", error);
      toast.error("Failed to upload file");
    } finally {
      setIsUploading(false);
    }
  };

  if (isLoading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <p className="text-xl">Loading...</p>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <nav className="bg-white shadow">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between h-16">
            <div className="flex">
              <div className="flex-shrink-0 flex items-center">
                <h1 className="text-xl font-bold text-indigo-600">
                  U-Management
                </h1>
              </div>
            </div>
            <div className="flex items-center">
              {user && (
                <div className="mr-4">
                  <span className="text-gray-700">
                    Welcome,{" "}
                    <span className="font-medium">
                      {user.firstName} {user.lastName}
                    </span>
                  </span>
                  <span className="ml-2 inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-indigo-100 text-indigo-800">
                    {user.role}
                  </span>
                </div>
              )}
              <button
                onClick={logout}
                className="ml-4 px-4 py-2 border border-transparent text-sm font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700"
              >
                Logout
              </button>
            </div>
          </div>
        </div>
      </nav>

      <div className="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
        <div className="px-4 py-6 sm:px-0">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            {/* User Generation Section */}
            <div className="bg-white overflow-hidden shadow rounded-lg">
              <div className="px-4 py-5 sm:p-6">
                <h3 className="text-lg leading-6 font-medium text-gray-900">
                  Generate Users
                </h3>
                <div className="mt-2 max-w-xl text-sm text-gray-500">
                  <p>Generate random users with realistic data.</p>
                </div>
                <div className="mt-5">
                  <div className="flex items-center">
                    <label
                      htmlFor="userCount"
                      className="mr-2 block text-sm font-medium text-gray-700"
                    >
                      Number of users:
                    </label>
                    <input
                      type="number"
                      id="userCount"
                      min="1"
                      max="100"
                      value={userCount}
                      onChange={(e) => setUserCount(parseInt(e.target.value))}
                      className="block w-20 sm:text-sm rounded-md text-black px-3 py-2"
                    />
                  </div>
                  <button
                    type="button"
                    onClick={handleGenerateUsers}
                    disabled={generatingUsers}
                    className="mt-3 inline-flex items-center px-4 py-2 border border-transparent shadow-sm text-sm font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
                  >
                    {generatingUsers ? "Generating..." : "Generate Users"}
                  </button>
                </div>
              </div>
            </div>

            {/* Batch Import Section */}
            <div className="bg-white overflow-hidden shadow rounded-lg">
              <div className="px-4 py-5 sm:p-6">
                <h3 className="text-lg leading-6 font-medium text-gray-900">
                  Batch Import Users
                </h3>
                <div className="mt-2 max-w-xl text-sm text-gray-500">
                  <p>Upload a JSON file to import multiple users at once.</p>
                </div>
                <div className="mt-5">
                  <div className="flex items-center">
                    <input
                      type="file"
                      accept=".json"
                      onChange={handleFileChange}
                      className="block w-full text-sm text-gray-500 file:mr-4 file:py-2 file:px-4 file:rounded-md file:border-0 file:text-sm file:font-medium file:bg-indigo-50 file:text-indigo-700 hover:file:bg-indigo-100"
                    />
                  </div>
                  <button
                    type="button"
                    onClick={handleFileUpload}
                    disabled={isUploading || !selectedFile}
                    className="mt-3 inline-flex items-center px-4 py-2 border border-transparent shadow-sm text-sm font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
                  >
                    {isUploading ? "Uploading..." : "Upload File"}
                  </button>
                </div>

                {uploadResult && (
                  <div className="mt-4 p-4 bg-gray-50 rounded-md">
                    <h4 className="text-sm font-medium text-gray-900">
                      Upload Results:
                    </h4>
                    <ul className="mt-2 text-sm text-gray-500">
                      <li>Total Records: {uploadResult.totalRecords}</li>
                      <li>
                        Successfully Imported: {uploadResult.successCount}
                      </li>
                      <li>Failed to Import: {uploadResult.failedCount}</li>
                    </ul>
                  </div>
                )}
              </div>
            </div>

            {/* Profile Section */}
            {user && (
              <div className="bg-white overflow-hidden shadow rounded-lg md:col-span-2">
                <div className="px-4 py-5 sm:p-6">
                  <h3 className="text-lg leading-6 font-medium text-gray-900">
                    Your Profile
                  </h3>
                  <div className="mt-5 grid grid-cols-1 gap-5 sm:grid-cols-2 lg:grid-cols-3">
                    <div>
                      <dt className="text-sm font-medium text-gray-500">
                        Full name
                      </dt>
                      <dd className="mt-1 text-sm text-gray-900">
                        {user.firstName} {user.lastName}
                      </dd>
                    </div>
                    <div>
                      <dt className="text-sm font-medium text-gray-500">
                        Email
                      </dt>
                      <dd className="mt-1 text-sm text-gray-900">
                        {user.email}
                      </dd>
                    </div>
                    <div>
                      <dt className="text-sm font-medium text-gray-500">
                        Username
                      </dt>
                      <dd className="mt-1 text-sm text-gray-900">
                        {user.username}
                      </dd>
                    </div>
                    <div>
                      <dt className="text-sm font-medium text-gray-500">
                        Role
                      </dt>
                      <dd className="mt-1 text-sm text-gray-900">
                        {user.role}
                      </dd>
                    </div>
                  </div>
                </div>
              </div>
            )}

            {/* User Search (Admin Only) */}
            <UserSearch />
          </div>
        </div>
      </div>
    </div>
  );
}
