db.getCollection("content").updateMany(
    {
        partnerId: 'SHEMAROOME',
        'playableContent.subcategory': 'TRAILER'
    },
    {
        $set: {
            "metadata.contentState": [
                {
                    dropdownId: "EXPIRED",
                    name: "Expired",
                    additionalProperties: {},
                    _class: "in.wynk.msp.dto.cms.content.DropdownEntry"
                }
            ],
            archivedMetadata: {
                contentState: [
                    {
                        dropdownId: "PUBLISH",
                        name: "Publish",
                        additionalProperties: {},
                        _class: "in.wynk.msp.dto.cms.content.DropdownEntry"
                    }
                ]
            }
        }
    }
)