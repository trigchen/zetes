/*
 * Mesh.cpp
 *
 *  Created on: Jun 19, 2013
 *      Author: imizus
 */

#include "Mesh.h"

namespace cubex
{
	void Mesh::addVertex(glm::vec3 vertex)
	{
		vertices.push_back(vertex);
	}
	void Mesh::addNormal(glm::vec3 normal)
	{
		normals.push_back(normal);
	}
	void Mesh::addTextureCoords(glm::vec2 texCoord)
	{
		textureCoords.push_back(texCoord);
	}

	bool Mesh::checkFace3(const Face3& face)
	{
		unsigned int verticesCount = vertices.size();


		if (face.vertexIndex1 >= verticesCount)	return false;
		if (face.vertexIndex2 >= verticesCount)	return false;
		if (face.vertexIndex3 >= verticesCount)	return false;

		if (face.containsNormals)
		{
			unsigned int normalsCount = normals.size();

			if (face.normalIndex1 >= normalsCount)	return false;
			if (face.normalIndex2 >= normalsCount)	return false;
			if (face.normalIndex3 >= normalsCount)	return false;
		}

		if (face.containsTextureCoords)
		{
			unsigned int textureCoordsCount = textureCoords.size();

			if (face.textureCoordIndex1 >= textureCoordsCount)	return false;
			if (face.textureCoordIndex2 >= textureCoordsCount)	return false;
			if (face.textureCoordIndex3 >= textureCoordsCount)	return false;
		}

		return true;
	}

	bool Mesh::checkFace4(const Face4& face)
	{
		unsigned int verticesCount = vertices.size();

		if (face.vertexIndex1 >= verticesCount)	return false;
		if (face.vertexIndex2 >= verticesCount)	return false;
		if (face.vertexIndex3 >= verticesCount)	return false;
		if (face.vertexIndex4 >= verticesCount)	return false;

		if (face.containsNormals)
		{
			unsigned int normalsCount = normals.size();

			if (face.normalIndex1 >= normalsCount)	return false;
			if (face.normalIndex2 >= normalsCount)	return false;
			if (face.normalIndex3 >= normalsCount)	return false;
			if (face.normalIndex4 >= normalsCount)	return false;
		}

		if (face.containsTextureCoords)
		{
			unsigned int textureCoordsCount = textureCoords.size();

			if (face.textureCoordIndex1 >= textureCoordsCount)	return false;
			if (face.textureCoordIndex2 >= textureCoordsCount)	return false;
			if (face.textureCoordIndex3 >= textureCoordsCount)	return false;
			if (face.textureCoordIndex4 >= textureCoordsCount)	return false;
		}

		return true;
	}

	void Mesh::addFace3(Face3 face)
	{
		if (!checkFace3(face))
		{
			throw CubexException("Incorrect Face3 data");
		}
		faces3.push_back(face);
	}
	void Mesh::addFace4(Face4 face)
	{

		if (!checkFace4(face))
		{
			throw CubexException("Incorrect Face4 data");
		}
		faces4.push_back(face);
	}

} /* namespace cubex */
