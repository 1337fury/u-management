"use client";

import { useState } from "react";
import { useAuth } from "@/context/AuthContext";
import { userAPI } from "@/services/api";
import toast from "react-hot-toast";

export default function UserSearch() {
  const { user } = useAuth();
  const [searchUsername, setSearchUsername] = useState("");
  const [searchResult, setSearchResult] = useState<any>(null);
  const [isSearching, setIsSearching] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // Only admins can search for users
  if (user?.role !== "ADMIN") {
    return null;
  }

  const handleSearch = async () => {
    if (!searchUsername.trim()) {
      toast.error("Please enter a username");
      return;
    }

    setIsSearching(true);
    setError(null);
    setSearchResult(null);

    try {
      const userData = await userAPI.getUserByUsername(searchUsername);
      setSearchResult(userData);
    } catch (error: any) {
      console.error("Error searching for user:", error);
      setError(
        error.response?.status === 404
          ? "User not found"
          : "An error occurred while searching for the user"
      );
      toast.error("User search failed");
    } finally {
      setIsSearching(false);
    }
  };

  return (
    <div className="bg-white overflow-hidden shadow rounded-lg md:col-span-2">
      <div className="px-4 py-5 sm:p-6">
        <h3 className="text-lg leading-6 font-medium text-gray-900">
          User Search (Admin Only)
        </h3>
        <div className="mt-2 max-w-xl text-sm text-gray-500">
          <p>Search for a user by username.</p>
        </div>
        <div className="mt-5">
          <div className="flex items-center">
            <input
              type="text"
              value={searchUsername}
              onChange={(e) => setSearchUsername(e.target.value)}
              placeholder="Enter username"
              className="block w-full sm:text-sm rounded-md text-black px-3 py-2"
            />
            <button
              type="button"
              onClick={handleSearch}
              disabled={isSearching}
              className="ml-3 inline-flex items-center px-4 py-2 border border-transparent shadow-sm text-sm font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
            >
              {isSearching ? "Searching..." : "Search"}
            </button>
          </div>
        </div>

        {error && (
          <div className="mt-4 p-4 bg-red-50 rounded-md">
            <p className="text-sm text-red-700">{error}</p>
          </div>
        )}

        {searchResult && (
          <div className="mt-4 p-4 bg-gray-50 rounded-md">
            <h4 className="text-sm font-medium text-gray-900">User Found:</h4>
            <div className="mt-3 grid grid-cols-1 gap-5 sm:grid-cols-2 lg:grid-cols-3">
              <div>
                <dt className="text-sm font-medium text-gray-500">Full name</dt>
                <dd className="mt-1 text-sm text-gray-900">
                  {searchResult.firstName} {searchResult.lastName}
                </dd>
              </div>
              <div>
                <dt className="text-sm font-medium text-gray-500">Email</dt>
                <dd className="mt-1 text-sm text-gray-900">
                  {searchResult.email}
                </dd>
              </div>
              <div>
                <dt className="text-sm font-medium text-gray-500">Username</dt>
                <dd className="mt-1 text-sm text-gray-900">
                  {searchResult.username}
                </dd>
              </div>
              <div>
                <dt className="text-sm font-medium text-gray-500">Role</dt>
                <dd className="mt-1 text-sm text-gray-900">
                  {searchResult.role}
                </dd>
              </div>
              <div>
                <dt className="text-sm font-medium text-gray-500">Company</dt>
                <dd className="mt-1 text-sm text-gray-900">
                  {searchResult.company}
                </dd>
              </div>
              <div>
                <dt className="text-sm font-medium text-gray-500">
                  Job Position
                </dt>
                <dd className="mt-1 text-sm text-gray-900">
                  {searchResult.jobPosition}
                </dd>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
